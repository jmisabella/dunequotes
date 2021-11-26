package models.behaviors

import models.classes.QuoteBank
import models.utilities.RNG
import models.behaviors._
import java.text.SimpleDateFormat
import java.util.Date

trait Configuration {
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
  def initialState(quotesConfigPath: String, historyConfigPath: String): Either[String, QuoteBank] = {
    val quotes = reader.readFile(quotesConfigPath)
    val history = reader.readFile(historyConfigPath)
    (quotes, history) match {
      case (Left(q), Left(h)) => Left(s"Error reading both quotes and history: [$q], [$h]")
      case (Left(q), _) => Left(s"Error reading quotes: [$q]")
      case (_, Left(h)) => Left(s"Error reading history: [$h]")
      case (Right(rawQuotes), Right(rawHistory)) => {
        (serialization.parse(rawQuotes), serialization.parse(rawHistory)) match {
          case (Left(q), Left(h)) => Left(s"Error deserializing both quotes and history: [$q], [$h]")
          case (Left(q), _) => Left(s"Error deserializing quotes: [$q]") 
          case (_, Left(h)) => Left(s"Error deserializing history: [$h]")
          case (Right(qs), Right(hs)) => {
            // now need to get last modified date from history in order to see if it is time to rollover...
            val historyModifiedTime: Either[String, String] = reader.lastModified(historyConfigPath)
            historyModifiedTime match {
              case Left(m) => Left(m)
              case Right(m) => {
                // now need to see if it is time to rollover
                val format = "yyyy-MM-dd HH:mm:ss.SSS"
                val currentTime = new SimpleDateFormat(format).format(new Date())
                val isRollover = rollover.isRollover(m, currentTime, format)
                isRollover match {
                  case false => {
                    // not yet time to rollover, so create QuotesBank with `m` as current time...
                    Right(QuoteBank(qs, hs, 30, RNG.RandomSeed(qs.length), m))
                  }
                  case true => {
                    // time to rollover, so need to determine next featured quote and write it to history config 
                    val previousState = QuoteBank(qs, hs, 30, RNG.RandomSeed(qs.length), m)
                    val nextState: QuoteBank = getQuote.rotateFeaturedQuote(previousState) 
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
        }
      }
    }
  }
}