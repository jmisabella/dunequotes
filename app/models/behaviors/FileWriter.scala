package models.behaviors

import models.utilities.SyntaxtHelpers._
import scala.io.Source

trait FileWriter {
  // TODO: test
  def writeToFile(fileName: String, data: String) = using (new java.io.FileWriter(fileName)) {
    fileWriter => fileWriter.write(data)
  }
}