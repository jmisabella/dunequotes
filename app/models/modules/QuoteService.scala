package models.modules

import models.behaviors._

private object fileReaderService extends FileReader
private object fileWriterService extends FileWriter
private object serializationService extends QuoteSerialization
private object rolloverService extends TimeRollover 
private object getQuoteService extends GetQuote

object QuoteService extends StateManagement {
  override type _FileReader = FileReader
  override type _FileWriter = FileWriter
  override type _QuoteSerialization = QuoteSerialization
  override type _TimeRollover = TimeRollover
  override type _GetQuote = GetQuote
  override val reader: FileReader = fileReaderService
  override val writer: FileWriter = fileWriterService
  override val serialization: QuoteSerialization = serializationService
  override val rollover: TimeRollover = rolloverService
  override val getQuote: GetQuote = getQuoteService
  

} 