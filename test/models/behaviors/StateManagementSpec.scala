package models.behaviors

import models.behaviors._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File

class StateManagementSpec extends AnyFlatSpec with BeforeAndAfterEach {
  private val singleQuote = """{"quotes":[{"source":"Franklin D. Roosevelt","quote":"The only thing we have to fear is fear itself."}]}"""
  private val multipleQuotes = """{"quotes":[
    {
      "source":"Dune",
      "quote":"The day the flesh shapes and the flesh the day shapes"
    },
    {
      "source":"Dune",
      "quote":"A time to get and a time to lose"
    },
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
      "quote": "Wealth is a tool of freedom. But the pursuit of wealth is the way to slavery."
    }, 
    {
      "source": "God Emperor of Dune",
      "quote": "The Golden Path endures."
    }
  ]}"""
  private val emptyHistory = """{"quotes":[ ]}"""
  private val emptyQuotes = """{"quotes":[ ]}"""

  private val singleQuoteFileName = "singleQuote.json"
  private val multipleQuoteFileName = "multipleQuotes.json"
  private val emptyHistoryFileName = "emptyHistory.json"
  private val emptyQuotesFileName = "emptyQuotes.json"

  override def beforeEach(): Unit = {
    var fileWriter = new java.io.FileWriter(multipleQuoteFileName)
    try {
      fileWriter.write(multipleQuotes)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(singleQuoteFileName)
    try {
      fileWriter.write(singleQuote)
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
  }

  override def afterEach(): Unit = {
    new File(multipleQuoteFileName).delete
    new File(singleQuoteFileName).delete
    new File(emptyHistoryFileName).delete
    new File(emptyQuotesFileName).delete
  }

  // TODO
  // "StateManagement behavior" should "???" in {
  //   ???
  // }

  // it should "???" in {
  //   ???
  // }

}
