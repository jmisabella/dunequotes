package models.behaviors

import models.behaviors.FileReader
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach
import java.io.File

class FileReaderSpec extends AnyFlatSpec with BeforeAndAfterEach {
  case object reader extends FileReader

  val emptyFileContents = ""
  val singleLineFileContents = "Hello world!!"
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
  private var fileReader: java.io.FileReader = null 

  override def beforeEach(): Unit = {
    var fileWriter = new java.io.FileWriter(emptyFileName)
    try {
      fileWriter.write(emptyFileContents)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(multiLineFileName)
    try {
      fileWriter.write(multiLineFileContents)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(singleLineFileName)
    try {
      fileWriter.write(singleLineFileContents)
    } finally {
      fileWriter.close()
    }
  }

  override def afterEach(): Unit = {
    new File(emptyFileName).delete
    new File(multiLineFileName).delete
    new File(singleLineFileName).delete
  }

  "FileReader" should "read from an empty file" in {
    val contents = reader.readFile(emptyFileName)
    assert(contents.isRight && contents.getOrElse("ERROR OCCURRED") == emptyFileContents, "Error occurrred reading from empty file")
  }
  
  it should "read from single-line file" in {
    val contents = reader.readFile(singleLineFileName)
    assert(contents.isRight && contents.getOrElse("") == singleLineFileContents, "Error occurred reading from single-line file")
  }

  it should "read from multi-line file" in {
    val contents = reader.readFile(multiLineFileName)
    assert(contents.isRight && contents.getOrElse("") == multiLineFileContents, "Error occurred reading from multi-line file")
  }
  
  it should "read non-existent file without throwing an exception but would consider it an error case" in {
    val contents = reader.readFile(nonExistentFileName)
    assert(contents.isLeft, "Expected error when reading non-existing but actually read the file without any errors! File contents: [${contents.getOrElse(None)}]")
  }

  it should "get last-modified date from single-line file" in {
    val lastModified = reader.lastModified(singleLineFileName)
    println(s"LAST MODIFIED: [$lastModified]")
    assert(lastModified.isRight, s"Expected no error, actually observed: [${lastModified.getOrElse(None)}]")
    assert(lastModified.getOrElse(None) != null && lastModified.getOrElse(None) != "", s"Expected non-empty value, actually observed: [${lastModified.getOrElse(None)}]")
  }

  it should "get last-modified date from multi-line file" in {
    val lastModified = reader.lastModified(multiLineFileName)
    println(s"LAST MODIFIED: [$lastModified]")
    assert(lastModified.isRight, s"Expected no error, actually observed: [${lastModified.getOrElse(None)}]")
    assert(lastModified.getOrElse(None) != null && lastModified.getOrElse(None) != "", s"Expected non-empty value, actually observed: [${lastModified.getOrElse(None)}]")
  }

  it should "get attempt to get last-modified date from empty file" in {
    val lastModified = reader.lastModified(emptyFileName)
    println(s"LAST MODIFIED: [$lastModified]")
    assert(lastModified.isRight, s"Expected error but no error occurred, actually observed: [${lastModified.getOrElse(None)}]")
  }

}