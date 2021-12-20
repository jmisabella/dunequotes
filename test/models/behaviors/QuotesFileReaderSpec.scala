package models.behaviors

import models.behaviors.{ FileReader, QuoteSerialization }
import models.classes.Quote
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File

class QuotesFileReaderSpec extends AnyFlatSpec with BeforeAndAfterEach {
  case object reader extends FileReader
  case object parser extends QuoteSerialization 

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

  override def beforeEach(): Unit = {
    var fileWriter = new java.io.FileWriter(multipleQuoteFileName)
    try {
      fileWriter.write(multipleQuoteContents)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(singleQuoteFileName)
    try {
      fileWriter.write(singleQuoteContents)
    } finally {
      fileWriter.close()
    }
  }

  override def afterEach(): Unit = {
    new File(multipleQuoteFileName).delete
    new File(singleQuoteFileName).delete
  }

  "FileReader and QuoteSerialization" should "read single quote" in {
    val contents = reader.readFile(singleQuoteFileName)
    contents match {
      case Left(e) => assert(false, s"Error reading from file [$singleQuoteFileName]: $e")
      case Right(s) => parser.parse(s) match {
        case Left(e) => assert(false, s"Error parsing single quote from [$singleQuoteFileName]: $e")
        case Right(qs) => {
          assert(
            qs.length == 1 && qs.head.source == "Franklin D. Roosevelt" && qs.head.quote == "The only thing we have to fear is fear itself.", 
            s"Error parsing single quote: expected [$singleQuoteContents], actual [${qs.head}]")
        }
      }
    }
  } 

  they should "read multiple quotes" in {
    val contents = reader.readFile(multipleQuoteFileName)
    contents match {
      case Left(e) => assert(false, s"Error reading from file [$singleQuoteFileName]: $e")
      case Right(s) => parser.parse(s) match {
        case Left(e) => assert(false, s"Error parsing single quote from [$singleQuoteFileName]: $e")
        case Right(qs) => {
          assert(
            qs.length == 3 && qs.map(_.source).distinct.length == 1 && qs.map(_.source).distinct.head == "Dune", 
            s"""Error parsing multiple quotes: expected [$multipleQuoteContents], actual [${qs.mkString("\n")}]""")
        }
      }
    }
  } 
}
