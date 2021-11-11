package models.behaviors

import models.classes.{ Quote, Quotes }
import play.api.libs.json.{ Json, JsSuccess }
import com.fasterxml.jackson.core.JsonParseException

trait ParseQuotes {
  def parse(sourceJson: String): Either[String, Seq[Quote]] = {
    try {
      Json.parse(sourceJson).validate[Quotes] match {
        case JsSuccess(q, _) => Right(q.quotes)
        case e => Left("Error occurred: " + e.toString())
      }
    } catch {
      case e: JsonParseException => Left(e.getMessage())
    }
  }

  def json(quotes: Seq[Quote]): String  = {
    Quotes(quotes).toString()
  }
}