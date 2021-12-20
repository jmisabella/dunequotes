package models.behaviors

import models.behaviors.FileWriter
import models.utilities.SyntaxtHelpers._
import scala.io.Source
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File

class FileWriterSpec extends AnyFlatSpec with BeforeAndAfterEach {
  case object writer extends FileWriter

  private val emptyContents = ""
  private val singleLineContents = "hello world!"
  val multiLineFileContents = 
    s"""All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy.  
All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy. 
All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy.  All work and no play makes Jack a dull boy. 
All
  work
    and
      no
        play
          makes Jack a dull boy. All work and no play
                                                      makes
                                                        Jack
                                                          a
                                                            dull
                                                              boy.  
    """

  private val emptyFileName = "Empty.txt"
  private val singleLineFileName = "HelloWorld.txt"
  private val multiLineFileName = "TheShining.txt"
  private val nonExistentFileName = "NonExistent.txt"

  override def afterEach(): Unit = {
    new File(emptyFileName).delete
    new File(multiLineFileName).delete
    new File(singleLineFileName).delete
  }

  "FileWriter" should "write a single line to a file" in {
    writer.writeToFile(singleLineFileName, singleLineContents) 
    var actualResult: String = "" 
    using (Source.fromFile(singleLineFileName)) {
      reader => {
        actualResult = (for (line <- reader.getLines()) yield line).mkString("\n")
      }
    }
    assert(actualResult == singleLineContents, s"Error writing single-line to file: expected result: [$singleLineContents], actual result: [$actualResult]")
  }

  it should "write multiple lines to a file" in {
    writer.writeToFile(multiLineFileName, multiLineFileContents) 
    var actualResult: String = "" 
    using (Source.fromFile(multiLineFileName)) {
      reader => {
        actualResult = (for (line <- reader.getLines()) yield line).mkString("\n")
      }
    }
    assert(actualResult == multiLineFileContents, s"Error writing multi-line to file: expected result: [$multiLineFileContents], actual result: [$actualResult]")
  }

  it should "write empty string to a file" in {
    writer.writeToFile(emptyFileName, emptyContents) 
    var actualResult: String = "ERROR OCCURRED" 
    using (Source.fromFile(emptyFileName)) {
      reader => {
        actualResult = (for (line <- reader.getLines()) yield line).mkString("\n")
      }
    }
    assert(actualResult == emptyContents, s"Error writing multi-line to file: expected result: [$emptyContents], actual result: [$actualResult]")
  }

}
