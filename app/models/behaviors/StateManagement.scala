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
  val getQuote: _GetQuote 

  // rotate history if neccessary, will either return existing state or the next state if history is rotated
  def state(currentState: State, quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, rolloverTimeIntervalUnit: Int = Calendar.DATE, randomSeed: Int = Random.nextInt()): Either[String, State] = {
    currentState match {
      case null => initialState(quotesConfigPath, historyConfigPath, historyLimit, randomSeed)
      case s => {
        // now need to see if it is time to rollover
        val format = "yyyy-MM-dd HH:mm:ss.SSS"
        val currentTime = new SimpleDateFormat(format).format(new Date())
        println("CALENDAR UNIT: " + rolloverTimeIntervalUnit)
        val isRollover = rollover.isRollover(currentState.rolloverTime, currentTime, format, rolloverTimeIntervalUnit)
        // val isRollover = rollover.isRollover(currentState.rolloverTime, currentTime, format, Calendar.DATE)
        isRollover match {
          case false => Right(s) // not yet time to rollover
          case true => {
            // time to rollover, so determine next featured quote and write it to history config 
            val nextState: State = getQuote.rotateFeaturedQuote(s) 
            try {
              writer.writeToFile(historyConfigPath, serialization.json(nextState.history))
            } catch {
              case e: Exception => Left(s"Error writing history to [$historyConfigPath]: ${e.getMessage()}")
            }
            Right(nextState.copy(rolloverTime = currentTime))  
          }
        }
      }
    } 
  }

  // def state(currentState: State, quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, rolloverTimeIntervalUnit: Int = Calendar.DATE, randomSeed: Int = Random.nextInt()): Either[String, State] = {
  //   currentState match {
  //     case null => initialState(quotesConfigPath, historyConfigPath, historyLimit, randomSeed)
  //     case s => {
  //       // now need to see if it is time to rollover
  //       val format = "yyyy-MM-dd HH:mm:ss.SSS"
  //       val currentTime = new SimpleDateFormat(format).format(new Date())
  //       println("CALENDAR UNIT: " + rolloverTimeIntervalUnit)
  //       val isRollover = rollover.isRollover(currentState.rolloverTime, currentTime, format, rolloverTimeIntervalUnit)
  //       // val isRollover = rollover.isRollover(currentState.rolloverTime, currentTime, format, Calendar.DATE)
  //       isRollover match {
  //         case false => Right(s) // not yet time to rollover
  //         case true => {
  //           // time to rollover, so determine next featured quote and write it to history config 
  //           val nextState: State = getQuote.rotateFeaturedQuote(s) 
  //           try {
  //             writer.writeToFile(historyConfigPath, serialization.json(nextState.history))
  //           } catch {
  //             case e: Exception => Left(s"Error writing history to [$historyConfigPath]: ${e.getMessage()}")
  //           }
  //           Right(nextState.copy(rolloverTime = currentTime))  
  //         }
  //       }
  //     }
  //   } 
  // }

  def initialState(quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, rolloverTimeIntervalUnit: Int = Calendar.DATE, randomSeed: Int = Random.nextInt()): Either[String, State] = {
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
          case Left(m) => Left(m)
          case Right(m) => {
            state(
              State(qs, hs, historyLimit, RNG.RandomSeed(randomSeed), m, rolloverTimeIntervalUnit), 
              quotesConfigPath, 
              historyConfigPath, 
              historyLimit, 
              rolloverTimeIntervalUnit)
          }
        }
      }
    }
  }
}