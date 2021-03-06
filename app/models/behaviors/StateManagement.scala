package models.behaviors

import models.classes.State
import models.utilities.RNG
import models.behaviors._
import scala.util.Random
import java.text.SimpleDateFormat
import java.util.{ Date, Calendar }

trait StateManagement {
  type _FileReader <: FileReader
  type _FileWriter <: FileWriter
  type _QuoteSerialization <: QuoteSerialization
  type _TimeRollover <: TimeRollover
  type _GetQuote <: GetQuote
  val reader: _FileReader
  val writer: _FileWriter
  val serialization: _QuoteSerialization
  val rollover: _TimeRollover
  val get: _GetQuote 

  def initialState(quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, rolloverTimeIntervalUnit: Int = Calendar.DATE, randomSeed: Int = Random.nextInt()): Either[String, State] = {
    // if history doesn't yet exist, then create an empty history
    if (!reader.exists(historyConfigPath) || reader.isEmpty(historyConfigPath)) {
      writer.writeToFile(historyConfigPath, """{"quotes":[]}""")
    }
    val quotes = reader.readFile(quotesConfigPath)
    val history = reader.readFile(historyConfigPath)
    (quotes, history) match {
      case (Left(q), Left(h)) => Left(s"Error reading both quotes and history: [$q], [$h]")
      case (Left(q), _) => Left(s"Error reading quotes: [$q]")
      case (_, Left(h)) => Left(s"Error reading history: [$h]")
      case (Right(rawQuotes), Right(rawHistory)) => (serialization.parse(rawQuotes), serialization.parse(rawHistory)) match {
        case (Left(q), Left(h)) => Left(s"Error deserializing both quotes and history: [$q], [$h]")
        case (Left(q), _) => Left(s"Error deserializing quotes: [$q]") 
        case (_, Left(h)) => Left(s"Error deserializing history: [$h]")
        case (Right(qs), Right(hs)) => reader.lastModified(historyConfigPath) match { // determine time of last rollover 
          case Left(e) => Left(e)
          case Right(m) => {
            hs.length match { // if history is empty, then force a rollover in order to persist item in history 
              case 0 => nextState(State(qs, hs, historyLimit, RNG.RandomSeed(randomSeed), historyConfigPath, m, rolloverTimeIntervalUnit), true)
              case _ => nextState(State(qs, hs, historyLimit, RNG.RandomSeed(randomSeed), historyConfigPath, m, rolloverTimeIntervalUnit))
            } 
          }
        }
      }
    }
  }

  // rotate history if neccessary, will either return existing state or the next state if history is rotated
  def nextState(currentState: State, forceRollover: Boolean = false): Either[String, State] = {
    currentState match {
      case null => Left(s"State is missing")
      case s => {
        // now need to see if it is time to rollover
        val format = "yyyy-MM-dd HH:mm:ss.SSS"
        val currentTime = new SimpleDateFormat(format).format(new Date())
        val isRollover = forceRollover || rollover.isRollover(currentState.rolloverTime, currentTime, format, currentState.rolloverTimeIntervalUnit)
        isRollover match {
          case false => Right(s) // not yet time to rollover
          case true => {
            // time to rollover, so determine next featured quote and write it to history config 
            val nextState: State = get.rotateFeaturedQuote(s) 
            try {
              writer.writeToFile(nextState.historyConfigPath, serialization.json(nextState.history))
            } catch {
              case e: Exception => Left(s"Error writing history to [$nextState.historyConfigPath]: ${e.getMessage()}")
            }
            Right(nextState.copy(rolloverTime = currentTime))  
          }
        }
      }
    } 
  }

}