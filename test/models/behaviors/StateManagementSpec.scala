package models.behaviors

import models.behaviors._
import models.classes.{ Quote, State }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File
import java.util.Calendar

class StateManagementSpec extends AnyFlatSpec with BeforeAndAfterEach {
  private object fileReaderService extends FileReader
  private object fileWriterService extends FileWriter
  private object serializationService extends QuoteSerialization
  private object rolloverService extends TimeRollover 
  private object getQuoteService extends GetQuote
  private object mgmt extends FileReader with FileWriter with QuoteSerialization with TimeRollover with GetQuote with StateManagement {
    override type _FileReader = FileReader
    override type _FileWriter = FileWriter
    override type _QuoteSerialization = QuoteSerialization
    override type _TimeRollover = TimeRollover
    override type _GetQuote = GetQuote
    override val reader: FileReader = fileReaderService
    override val writer: FileWriter = fileWriterService
    override val serialization: QuoteSerialization = serializationService
    override val rollover: TimeRollover = rolloverService
    override val get: GetQuote = getQuoteService
  } 
  private val quotes = """{"quotes":[
    {
      "source":"Dune",
      "quote":"A time to keep and a time to cast away; a time for love and a time to hate; a time of war and a time of peace."
    },
    {
      "source": "Children of Dune",
      "quote": "Those who sought the future hoped to gain the winning gamble on tomorrow's race. Instead they found themselves trapped into a lifetime whose every heartbeat and anguished wail was known."
    }, 
    {
      "source": "Children of Dune",
      "quote": "The joy of living, its beauty is all bound up in the fact that life can surprise you"
    }, 
    {
      "source": "Children of Dune",
      "quote": "One discovers the future in the past, and both are part of a whole."
    }, 
    {
      "source": "God Emperor of Dune",
      "quote": "The Golden Path endures."
    }
  ]}"""
  private val emptyQuotes = """{"quotes":[ ]}"""
  private val emptyHistory = """{"quotes":[ ]}"""
  private val historySize1 = """{"quotes":[
    {
      "source": "Children of Dune",
      "quote": "Those who sought the future hoped to gain the winning gamble on tomorrow's race. Instead they found themselves trapped into a lifetime whose every heartbeat and anguished wail was known."
    } 
  ]}"""
  private val historySize2 = """{"quotes":[
    {
      "source": "Children of Dune",
      "quote": "Those who sought the future hoped to gain the winning gamble on tomorrow's race. Instead they found themselves trapped into a lifetime whose every heartbeat and anguished wail was known."
    }, 
    {
      "source":"Dune",
      "quote":"A time to keep and a time to cast away; a time for love and a time to hate; a time of war and a time of peace."
    }
  ]}"""
  private val historySize4 = """{"quotes":[
    {
      "source": "Children of Dune",
      "quote": "Those who sought the future hoped to gain the winning gamble on tomorrow's race. Instead they found themselves trapped into a lifetime whose every heartbeat and anguished wail was known."
    }, 
    {
      "source":"Dune",
      "quote":"A time to keep and a time to cast away; a time for love and a time to hate; a time of war and a time of peace."
    },
    {
      "source": "Children of Dune",
      "quote": "The joy of living, its beauty is all bound up in the fact that life can surprise you"
    }, 
    {
      "source": "Children of Dune",
      "quote": "One discovers the future in the past, and both are part of a whole."
    } 
  ]}"""
  private val historySize5 = """{"quotes":[
    {
      "source": "God Emperor of Dune",
      "quote": "The Golden Path endures."
    },
    {
      "source": "Children of Dune",
      "quote": "Those who sought the future hoped to gain the winning gamble on tomorrow's race. Instead they found themselves trapped into a lifetime whose every heartbeat and anguished wail was known."
    }, 
    {
      "source":"Dune",
      "quote":"A time to keep and a time to cast away; a time for love and a time to hate; a time of war and a time of peace."
    },
    {
      "source": "Children of Dune",
      "quote": "The joy of living, its beauty is all bound up in the fact that life can surprise you"
    }, 
    {
      "source": "Children of Dune",
      "quote": "One discovers the future in the past, and both are part of a whole."
    } 
  ]}"""

  private val quotesFileName = "quotes.json"
  private val nonExistentHistoryFileName = "nonExistentHistory.json"
  private val emptyQuotesFileName = "emptyQuotes.json"
  private val emptyHistoryFileName = "emptyHistory.json"
  private val historySize1FileName = "history1.json"
  private val historySize2FileName = "history2.json"
  private val historySize4FileName = "history4.json"
  private val historySize5FileName = "history5.json"

  override def beforeEach(): Unit = {
    var fileWriter = new java.io.FileWriter(quotesFileName)
    try {
      fileWriter.write(quotes)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(emptyHistoryFileName)
    try {
      fileWriter.write(emptyHistory)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(emptyQuotesFileName)
    try {
      fileWriter.write(emptyQuotes)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(historySize1FileName)
    try {
      fileWriter.write(historySize1)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(historySize2FileName)
    try {
      fileWriter.write(historySize2)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(historySize4FileName)
    try {
      fileWriter.write(historySize4)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(historySize5FileName)
    try {
      fileWriter.write(historySize5)
    } finally {
      fileWriter.close()
    }
  }

  override def afterEach(): Unit = {
    new File(quotesFileName).delete
    new File(nonExistentHistoryFileName).delete
    new File(emptyHistoryFileName).delete
    new File(emptyQuotesFileName).delete
    new File(historySize1FileName).delete
    new File(historySize2FileName).delete
    new File(historySize4FileName).delete
    new File(historySize5FileName).delete
  }

  private def readQuotes(source: String): Seq[Quote] = {
    val lines: String = mgmt.readFile(source) match {
      case Left(e) => throw new RuntimeException(e)
      case Right(ls) => ls
    }
    val quotes: Seq[Quote] = mgmt.parse(lines) match {
      case Left(e) => throw new RuntimeException(e)
      case Right(qs) => qs
    }
    quotes
  }

  "StateManagement behavior" should "initialize minute rollover state when history file is empty" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val history: Seq[Quote] = readQuotes(emptyHistoryFileName)
    val historyLimit: Int = 3
    val state: Either[String, State] = mgmt.initialState(quotesFileName, emptyHistoryFileName, historyLimit, Calendar.MINUTE)
    assert(state.isRight, s"Expected state to be initialized but an error occurred: $state")
    assert(state.getOrElse(State()).history.length == history.length + 1, s"Expected history length [${history.length + 1}], actual [${state.getOrElse(State()).history}]")
    assert(state.getOrElse(State()).quotes == quotes, s"Expected quotes [${quotes}], actual [${state.getOrElse(State()).quotes}]")
  }

  it should "successfully initialize minute rollover state when history file is missing" in {
    val historyLimit: Int = 3
    val state: Either[String, State] = mgmt.initialState(quotesFileName, nonExistentHistoryFileName, historyLimit, Calendar.MINUTE)
    assert(state.isRight, s"Expected successful state initialization when history file [$nonExistentHistoryFileName] does not exist, however state initialization failed: $state")
    assert(state.getOrElse(State()).history.length == 1, s"Expected history length [1] but history length was actually [${state.getOrElse(State()).history.length}]")
  }

  it should "initialize minute rollover state when history size is 1" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val history: Seq[Quote] = readQuotes(historySize1FileName)
    val historyLimit: Int = 3
    val stateCheck: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.MINUTE)
    assert(stateCheck.isRight, s"Expected state to be initialized but an error occurred: $stateCheck")
    val state: State = stateCheck.getOrElse(State()) 
    assert(state.history == history, s"Expected history [${history}], actual [${state.history}]")
    assert(state.quotes == quotes, s"Expected quotes [${quotes}], actual [${state.quotes}]")
  }


  it should "get second rollover state and NOT rollover when history size is 1 and 600 milliseconds have passed" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val initialHistory: Seq[Quote] = readQuotes(historySize1FileName)
    val historyLimit: Int = 3
    val state1Check: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.SECOND)
    assert(state1Check.isRight, s"Expected state to be initialized but an error occurred: $state1Check")
    val state1: State = state1Check.getOrElse(State())
    assert(state1.history == initialHistory, s"Expected history [${initialHistory}], actual [${state1.history}]")
    assert(state1.quotes == quotes, s"Expected quotes [${quotes}], actual [${state1.quotes}]")
    Thread.sleep(600)
    val state2Check: Either[String, State] = mgmt.nextState(state1)
    assert(state2Check.isRight, s"Expected state to be initialized but an error occurred: $state2Check")
    val state2: State = state2Check.getOrElse(State())
    assert(state2.history == initialHistory, s"Expected history [${initialHistory}], actual [${state2.history}]")
    assert(state2.quotes == quotes, s"Expected quotes [${quotes}], actual [${state2.quotes}]")
  }


  it should "get second rollover state and rollover when history size is 1 and 1 second has passed" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val initialHistory: Seq[Quote] = readQuotes(historySize1FileName)
    val historyLimit: Int = 3
    val state1Check: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.SECOND)
    assert(state1Check.isRight, s"Expected state to be initialized but an error occurred: $state1Check")
    val state1: State = state1Check.getOrElse(State())
    assert(state1.history == initialHistory, s"Expected history [${initialHistory}], actual [${state1.history}]")
    assert(state1.quotes == quotes, s"Expected quotes [${quotes}], actual [${state1.quotes}]")
    Thread.sleep(1000)
    val state2Check: Either[String, State] = mgmt.nextState(state1)
    assert(state2Check.isRight, s"Expected state to be initialized but an error occurred: $state2Check")
    val state2: State = state2Check.getOrElse(State())
    assert(state2.history.length == initialHistory.length + 1, s"Expected history length [${initialHistory.length + 1}], actual [${state2.history.length}]")
    assert(state2.quotes == quotes, s"Expected quotes [${quotes}], actual [${state2.quotes}]")
  }
  
  it should "get second rollover state and rollover when history size is 1 and 2 seconds have passed" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val initialHistory: Seq[Quote] = readQuotes(historySize1FileName)
    val historyLimit: Int = 3
    val state1Check: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.SECOND)
    assert(state1Check.isRight, s"Expected state to be initialized but an error occurred: $state1Check")
    val state1: State = state1Check.getOrElse(State())
    assert(state1.history == initialHistory, s"Expected history [${initialHistory}], actual [${state1.history}]")
    assert(state1.quotes == quotes, s"Expected quotes [${quotes}], actual [${state1.quotes}]")
    Thread.sleep(2000)
    val state2Check: Either[String, State] = mgmt.nextState(state1)
    assert(state2Check.isRight, s"Expected state to be initialized but an error occurred: $state2Check")
    val state2: State = state2Check.getOrElse(State())
    assert(state2.history.length == initialHistory.length + 1, s"Expected history length [${initialHistory.length + 1}], actual [${state2.history.length}]")
    assert(state2.quotes == quotes, s"Expected quotes [${quotes}], actual [${state2.quotes}]")
    val state3Check: Either[String, State] = mgmt.nextState(state2)
    assert(state3Check.isRight, s"Expected state to be initialized but an error occurred: $state3Check")
    val state3: State = state3Check.getOrElse(State())
    assert(state3.history.length == initialHistory.length + 1, s"Expected history length [${initialHistory.length + 1}], actual [${state3.history.length}]")
    assert(state3.quotes == quotes, s"Expected quotes [${quotes}], actual [${state3.quotes}]")
    val updatedHistory: Seq[Quote] = readQuotes(historySize1FileName)
    assert(updatedHistory == state3.history, s"Expected history [${updatedHistory}], actual [${state3.history}]")
  }
  
  it should "get minute rollover state but NOT rollover when history size is 1 and 5 seconds have passed" in {
    val quotes: Seq[Quote] = readQuotes(quotesFileName)
    val history: Seq[Quote] = readQuotes(historySize1FileName)
    val historyLimit: Int = 3
    val state1Check: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.MINUTE)
    assert(state1Check.isRight, s"Expected state to be initialized but an error occurred: $state1Check")
    val state1: State = state1Check.getOrElse(State())
    assert(state1.history == history, s"Expected history [${history}], actual [${state1.history}]")
    assert(state1.quotes == quotes, s"Expected quotes [${quotes}], actual [${state1.quotes}]")
    Thread.sleep(5000)
    val state2Check: Either[String, State] = mgmt.nextState(state1)
    assert(state2Check.isRight, s"Expected state to be initialized but an error occurred: $state2Check")
    val state2: State = state2Check.getOrElse(State())
    assert(state2.history == history, s"Expected history [${history}], actual [${state2.history}]")
    assert(state2.quotes == quotes, s"Expected quotes [${quotes}], actual [${state2.quotes}]")
    val updatedHistory: Seq[Quote] = readQuotes(historySize1FileName)
    assert(updatedHistory == state2.history, s"Expected history [${updatedHistory}], actual [${state2.history}]")
  }
  
  // it should "get minute rollover state and rollover when history size is 1 and 1 minute has passed" in {
  //   val quotes: Seq[Quote] = readQuotes(quotesFileName)
  //   val initialHistory: Seq[Quote] = readQuotes(historySize1FileName)
  //   val historyLimit: Int = 3
  //   val state1Check: Either[String, State] = mgmt.initialState(quotesFileName, historySize1FileName, historyLimit, Calendar.MINUTE)
  //   assert(state1Check.isRight, s"Expected state to be initialized but an error occurred: $state1Check")
  //   val state1: State = state1Check.getOrElse(State())
  //   assert(state1.history == initialHistory, s"Expected history [${initialHistory}], actual [${state1.history}]")
  //   assert(state1.quotes == quotes, s"Expected quotes [${quotes}], actual [${state1.quotes}]")
  //   Thread.sleep(60000)
  //   val state2Check: Either[String, State] = mgmt.nextState(state1)
  //   assert(state2Check.isRight, s"Expected state to be initialized but an error occurred: $state2Check")
  //   val state2: State = state2Check.getOrElse(State())
  //   assert(state2.history.length == initialHistory.length + 1, s"Expected history length [${initialHistory.length}], actual [${state2.history.length}]")
  //   assert(state2.quotes == quotes, s"Expected quotes [${quotes}], actual [${state2.quotes}]")
  // }

  // it should "initialize minute rollover state when history size is 2 and 25 seconds have passed" in {
  //   ???
  // }

  // it should "initialize minute rollover state when history size is 2 and 62 seconds have passed" in {
  //   ???
  // }

  // //////
  // it should "get minute rollover state when state is null and history file is missing and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history file is empty and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history size is 1 and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history size is 1 and 20 seconds have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history size is 1 and 1 minute has passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history size is 2 and 25 seconds have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state is null and history size is 2 and 62 seconds have passed" in {
  //   ???
  // }

  // //////
  // it should "get minute rollover state when state already exists and history file is missing and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history file is empty and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history size is 1 and 0 minutes have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history size is 1 and 20 seconds have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history size is 1 and 1 minute has passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history size is 2 and 25 seconds have passed" in {
  //   ???
  // }

  // it should "get minute rollover state when state already exists and history size is 2 and 62 seconds have passed" in {
  //   ???
  // }

}
