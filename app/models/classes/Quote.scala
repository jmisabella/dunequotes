package models.classes

import play.api.libs.json.{ Json, Format, JsValue }

case class Quote(source: String, quote: String)
object Quote {
  def apply(quote: String): Quote = Quote("unknown", quote)
  implicit val jsonFormat: Format[Quote] = Json.format[Quote]
}

case class Quotes(quotes: Seq[Quote]) {
  override def toString(): String = {
    (quotes.map { q => 
      Json.obj(
        "source" -> q.source,
        "quote" -> q.quote 
      ).toString()}).mkString("""{"quotes":[""", "," ,"]}") 
    } 
}
object Quotes {
  implicit val jsonFormat: Format[Quotes] = Json.format[Quotes]
}
