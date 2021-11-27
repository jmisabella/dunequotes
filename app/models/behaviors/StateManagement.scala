package models.behaviors

import models.classes.QuoteBank
import models.utilities.RNG
import models.behaviors._
import scala.util.Random
import java.text.SimpleDateFormat
import java.util.Date

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

  // TODO: test
  // rotate history if neccessary, will either return existing state or the next state if history is rotated
  def state(currentState: QuoteBank, quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, randomSeed: Int = Random.nextInt()): Either[String, QuoteBank] = {
    currentState match {
      case null => initialState(quotesConfigPath, historyConfigPath, historyLimit, randomSeed)
      case s => {
        // now need to see if it is time to rollover
        val format = "yyyy-MM-dd HH:mm:ss.SSS"
        val currentTime = new SimpleDateFormat(format).format(new Date())
        val isRollover = rollover.isRollover(currentState.rolloverTime, currentTime, format)
        isRollover match {
          case false => Right(s) // not yet time to rollover
          case true => {
            // time to rollover, so determine next featured quote and write it to history config 
            val nextState: QuoteBank = getQuote.rotateFeaturedQuote(s) 
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

  // TODO: test 
  def initialState(quotesConfigPath: String, historyConfigPath: String, historyLimit: Int = 60, randomSeed: Int = Random.nextInt()): Either[String, QuoteBank] = {
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
          case Right(m) => state(QuoteBank(qs, hs, historyLimit, RNG.RandomSeed(randomSeed), m), quotesConfigPath, historyConfigPath, historyLimit, randomSeed)
        }
      }
    }
  }
}