package models.behaviors

import models.utilities.SyntaxtHelpers._
import scala.io.{ Source, BufferedSource }
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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

  def lastModified(source: String): Either[String, String] = {
    try {
      val last: Long = new File(source).lastModified()
      val format: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
      val date: String = format.format(new Date(last))
      Right(date)
    } catch {
      case e: Exception => Left(s"Error getting last modified date from source [$source]: ${e.getMessage()}")
    }
  }
}