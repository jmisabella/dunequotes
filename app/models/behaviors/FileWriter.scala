package models.behaviors

import scala.io.Source

trait FileWriter {

  def using[A <: {def close(): Unit}, B](param: A)(f: A => B): B = {
    try { f(param) } finally { param.close() }
  }

  // TODO: test
  def writeToFile(fileName: String, data: String) = using (new java.io.FileWriter(fileName)) {
    fileWriter => fileWriter.write(data)
  }

}