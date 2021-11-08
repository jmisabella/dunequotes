package models.behaviors

import models.behaviors.FileReader
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.BeforeAndAfterEach

import java.io.File

class FileReaderSpec extends AnyFlatSpec with BeforeAndAfterEach {
  case object reader extends FileReader

  val emptyFileContents = ""
  val shiningFileContents = 
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

  val helloWorldFileContents = "Hello world!!"

  private val emptyFileName = "Empty.txt"
  private val shiningFileName = "TheShining.txt"
  private val helloWorldFileName = "HelloWorld.txt"
  private val nonExistentFileName = "NonExistent.txt"
  private var fileReader: java.io.FileReader = null 

  override def beforeEach(): Unit = {
    var fileWriter = new java.io.FileWriter(emptyFileName)
    try {
      fileWriter.write(emptyFileContents)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(shiningFileName)
    try {
      fileWriter.write(shiningFileContents)
    } finally {
      fileWriter.close()
    }
    fileWriter = new java.io.FileWriter(helloWorldFileName)
    try {
      fileWriter.write(helloWorldFileContents)
    } finally {
      fileWriter.close()
    }
  }

  override def afterEach(): Unit = {
    new File(emptyFileName).delete
    new File(shiningFileName).delete
    new File(helloWorldFileName).delete
  }

  "FileReader" should "read from an empty file" in {
    val contents = reader.readFile(emptyFileName)
    assert(contents.isRight && contents.getOrElse("ERROR OCCURRED") == emptyFileContents, "Error occurrred reading from empty file")
  }
  
  it should "read from single-line file" in {
    val contents = reader.readFile(helloWorldFileName)
    assert(contents.isRight && contents.getOrElse("") == helloWorldFileContents, "Error occurred reading from single-line file")
  }

  it should "read from multi-line file" in {
    val contents = reader.readFile(shiningFileName)
    assert(contents.isRight && contents.getOrElse("") == shiningFileContents, "Error occurred reading from multi-line file")
  }
  
  it should "read non-existent file without throwing an exception but would consider it an error case" in {
    val contents = reader.readFile(nonExistentFileName)
    assert(contents.isLeft, "Expected error when reading non-existing but actually read the file without any errors! File contents: [${contents.getOrElse(None)}]")
  }
}