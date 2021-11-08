package models.behaviors

import scala.io.{ Source, BufferedSource }

trait FileReader {
  def readFile(source: String): Either[String, String] = {
    var buffer: BufferedSource = null 
    val result: Either[String, String] = try {
      buffer = Source.fromFile(source)
      val data = (for (line <- buffer.getLines()) yield line).mkString("\n")
      Right(data)
    } catch {
      case e: Exception => Left(s"Error reading file [$source]: ${e.getMessage}")
    } finally {
      if (buffer != null) {
        buffer.close()
      }
    }
    result
  }
}