package models.behaviors

import models.utilities.SyntaxtHelpers._
import scala.io.{ Source, BufferedSource }

trait FileReader {
  def readFile(source: String): Either[String, String] = {
    try {
      using (Source.fromFile(source)) {
        result => {
          val data = (for (line <- result.getLines()) yield line).mkString("\n")
          Right(data)
        }
      }
    } catch {
      case e: Exception => Left(s"Error reading file [$source]: ${e.getMessage}")
    }
  }
}