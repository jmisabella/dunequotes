package models.behaviors

import models.classes.Quote
import org.scalatest.flatspec.AnyFlatSpec

class QuoteSerializationSpec extends AnyFlatSpec {

  case object parser extends QuoteSerialization

  "ParseQuotes trait" should "parse one quote" in {
    val source = "Elaine Benes"
    val quote = "yada, yada, yada..."
    val quotes: String = s"""
      { "quotes": [
          {
            "source": "$source",
            "quote": "$quote" 
          }
        ]
      }
    """
    val result: Either[String, Seq[Quote]] = parser.parse(quotes)
    assert(
      result.isRight && result.getOrElse(Nil) == Seq(Quote(source, quote)), 
      s"Expected source [$source], expected quote [$quote], actual result [$result]"
    )
  }

  it should "parse no quotes" in {
    val quotes = """{ "quotes": [] }"""
    val result = parser.parse(quotes)
    assert(
      result.isRight && result.getOrElse(Seq(Quote("error", "occurred"))) == Nil,
      s"Expected result [${Nil}], actual result [$result]"
    )
  }

  it should "parse two different quotes" in {
    val source1 = "Elaine Benes"
    val quote1 = "yada, yada, yada..."
    val source2 = "Franklin Delanor Roosevelt"
    val quote2 = "The only thing we have to fear is fear itself"
    val quotes: String = s"""
      { "quotes": [
          { "source": "$source1", "quote": "$quote1" },
          { "source": "$source2", "quote": "$quote2" }
        ]
      }
    """
    val result = parser.parse(quotes)
    assert(
      result.isRight && 
        result.getOrElse(Nil).length == 2 &&
        result.getOrElse(Nil).head == Quote(source1, quote1) &&
        result.getOrElse(Nil).tail.head == Quote(source2, quote2), 
      s"Expected ${Seq(Quote(source1, quote1), Quote(source2, quote2))}, actual [$result]"
    )
  }

  it should "parse two identical quotes" in {
    val source = "Franklin Delanor Roosevelt"
    val quote = "The only thing we have to fear is fear itself"
    val quotes: String = s"""
      { "quotes": [
          { "source": "$source", "quote": "$quote" },
          { "source": "$source", "quote": "$quote" }
        ]
      }
    """
    val result = parser.parse(quotes)
    assert(
      result.isRight && 
        result.getOrElse(Nil).length == 2 &&
        result.getOrElse(Nil).head == Quote(source, quote) &&
        result.getOrElse(Nil).tail.head == Quote(source, quote), 
      s"Expected ${Seq(Quote(source, quote), Quote(source, quote))}, actual [$result]"
    )
  }
  
  it should "consider invalid json to be an error" in {
    val quotes = """
      {
        "quotes": [
          { source: no_surrounding_quotes, quote: quote_with_no_surrounding_quotes }
        ]
      }
    """
    val result = parser.parse(quotes)
    assert(
      result.isLeft,
      s"Expected result to be an error since JSON is invalid, but actual result is not an error: actual result [$result]"
    )
  }

  it should "consider valid json which has a different schema to be an error" in {
    val quotes = """
      {
        { "source": "Roger Waters", "quote": "Picture your kid with his hand on the trigger. Picture prosthetics in Afghanistan." }
      }
    """
    val result = parser.parse(quotes)
    assert(
      result.isLeft, 
      s"Expected result to be an error JSON is of an unexpected schema, but actual result is not an error: actual result [$result]"
    )
  }

  it should "serialize two quotes into a JSON string" in {
    val source1 = "Franklin Delanor Roosevelt"
    val quote1 = "The only thing we have to fear is fear itself"
    val source2 = "Jack Torrence"
    val quote2 = "Here's Johnny!"
    val quotes: String = s"""{"quotes":[{"source":"$source1","quote":"$quote1"},{"source":"$source2","quote":"$quote2"}]}"""
    val result = parser.parse(quotes)
    result match {
      case Left(e) => assert(false, "Error occurred serializing quotes: " + e)
      case Right(qs) => {
        val serialized: String = parser.json(qs)
        assert(serialized == quotes, s"Expected [$quotes], actual [$serialized]")
      }
    }
  }
}