package models.behaviors

import models.behaviors.{ FileReader, FileWriter, QuoteSerialization }
import models.classes.Quote
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File

class QuotesFileWriterSpec extends AnyFlatSpec with BeforeAndAfterEach {
  case object quotesIO extends FileReader with FileWriter with QuoteSerialization 

  private val singleQuoteContents = """{"quotes":[{"source":"Franklin D. Roosevelt","quote":"The only thing we have to fear is fear itself."}]}"""

  private val multipleQuoteContents = """{"quotes":[
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
    }
  ]}"""

  private val singleQuoteFileName = "HelloWorld.txt"
  private val multipleQuoteFileName = "TheShining.txt"

  // removes double-space to account for indented JSON;
  // this will cause issues if source or quote contains a double-space
  private def trimMultiLine(value: String): String = value.replace("\n", "").replace("  ", "")
  
  override def afterEach(): Unit = {
    new File(singleQuoteFileName).delete
    new File(multipleQuoteFileName).delete
  }

  "QuoteSerialization with FileWriter" should "write a single quote to file" in {
    quotesIO.parse(singleQuoteContents) match {
      case Left(e) => assert(false, s"Error occurred parsing single quote: " + e)
      case Right(qs) => {
        val json = quotesIO.json(qs)
        assert(json == singleQuoteContents, s"Error serializing to json")
        quotesIO.writeToFile(singleQuoteFileName, quotesIO.json(qs))
        val result = quotesIO.readFile(singleQuoteFileName)
        result match {
          case Left(e) => assert(false, s"Error occurred reading file $singleQuoteFileName: " + e)
          case Right(s) => assert(s == singleQuoteContents, s"Expected [$singleQuoteContents], actual [$s]")
        }
      }
    }
  }

  they should "write multiple quotes to file" in {
    quotesIO.parse(multipleQuoteContents) match {
      case Left(e) => assert(false, s"Error occurred parsing multiple quotes: " + e)
      case Right(qs) => {
        val serialized = quotesIO.json(qs)
        assert(serialized == trimMultiLine(multipleQuoteContents), s"Error serializing to json")
        quotesIO.writeToFile(multipleQuoteFileName, quotesIO.json(qs))
        val result = quotesIO.readFile(multipleQuoteFileName)
        result match {
          case Left(e) => assert(false, s"Error occurred reading file $multipleQuoteFileName: " + e)
          case Right(s) => assert(s == trimMultiLine(multipleQuoteContents), s"Expected [${trimMultiLine(multipleQuoteContents)}], actual [$s]")
        }
      }
    }
  }

}