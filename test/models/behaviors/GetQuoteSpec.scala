package models.behaviors

import models.behaviors.{ GetQuote, QuoteSerialization }
import models.classes.{ State, Quote }
import models.utilities.RNG
import org.scalatest.flatspec.AnyFlatSpec

class GetQuotesSpec extends AnyFlatSpec {
  private case object parser extends QuoteSerialization
  private case object quoteManager extends GetQuote
  private val source1 = "Dune"
  private val quote1 = "The day the flesh shapes and the flesh the day shapes"
  private val quote2 = "A time to get and a time to lose"
  private val quote3 = "A time to keep and a time to cast away; a time for love and a time to hate; a time of war and a time of peace."
  private val source2 = "Dune Messiah"
  private val quote4 = "To endure oneself may be the hardest task in the universe"
  private val source3 = "Children of Dune"
  private val quote5 = "The joy of living, its beauty is all bound up in the fact that life can surprise you"
  private val rawQuotes: String = s"""{ "quotes": [
    {
      "source": "$source1",
      "quote": "$quote1"
    }, 
    {
      "source": "$source1",
      "quote": "$quote2"
    }, 
    {
      "source": "$source1",
      "quote": "$quote3"
    }, 
    {
      "source": "$source2",
      "quote": "$quote4"
    }, 
    {
      "source": "$source3",
      "quote": "$quote5"
    } 
  ]}"""
  private val seed = 532
  private val historyLimit = 3

  "GetQuotes trait" should "feature None when history is empty" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Nil
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val featured: Option[Quote] = quoteManager.featuredQuote(initialState)
    assert(featured.isEmpty, s"When history is empty, expected featured quote to also be empty, however got the value ${featured.getOrElse(None)}")
  }

  it should "feature the only quote in history when history length is one" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes.head)
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val featured: Option[Quote] = quoteManager.featuredQuote(initialState)
    assert(featured.isDefined, s"Expected featured quote to be defined, however featured quote is empty")
    assert(featured.get.source == source1, s"Expected source [$source1], actual source [${featured.get.source}]")
    assert(featured.get.quote == quote1, s"Expected quote [$quote1], actual quote [${featured.get.quote}]")
  }

  it should "feature the the last quote in history when history length is two" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes(0), quotes(1))
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val featured: Option[Quote] = quoteManager.featuredQuote(initialState)
    assert(featured.isDefined, s"Expected featured quote to be defined, however featured quote is empty")
    assert(featured.get.source == quotes(1).source, s"Expected source [${quotes(1).source}], actual source [${featured.get.source}]")
    assert(featured.get.quote == quotes(1).quote, s"Expected quote [${quotes(1).quote}], actual quote [${featured.get.quote}]")
  }

  it should "feature the the last quote in history when history length is three" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes(0), quotes(1), quotes(2))
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val featured: Option[Quote] = quoteManager.featuredQuote(initialState)
    assert(featured.isDefined, s"Expected featured quote to be defined, however featured quote is empty")
    assert(featured.get.source == quotes(2).source, s"Expected source [${quotes(2).source}], actual source [${featured.get.source}]")
    assert(featured.get.quote == quotes(2).quote, s"Expected quote [${quotes(2).quote}], actual quote [${featured.get.quote}]")
  }

  it should "add the only quote to history when rotating quote of a state without history" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Nil
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val nextState: State = quoteManager.rotateFeaturedQuote(initialState)
    val featured: Option[Quote] = quoteManager.featuredQuote(nextState)
    assert(nextState.history.length == 1, s"Expecting next state's history to have 1 quote, however next state's history length is [${nextState.history.length}]")
    assert(featured.isDefined, s"Expected next state's featured quote to exist but it is empty")
    assert(featured.get.quote == nextState.history(0).quote, s"Expected next state's featured quote to be [${nextState.history(0).quote}] but it was actually [${featured.get.quote}]")
  }

  it should "add one more quote to history of length 1 when rotating quote" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes(0))
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val nextState: State = quoteManager.rotateFeaturedQuote(initialState)
    val featured: Option[Quote] = quoteManager.featuredQuote(nextState)
    assert(nextState.history.length == 2, s"Expecting next state's history to have 2 quotes, however next state's history length is [${nextState.history.length}]")
    assert(featured.isDefined, s"Expected next state's featured quote to exist but it is empty")
    assert(featured.get.quote == nextState.history(1).quote, s"Expected next state's featured quote to be [${nextState.history(1).quote}] but it was actually [${featured.get.quote}]")
  }

  it should "add one more quote to history of length 2 when rotating quote" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes(0), quotes(1))
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val nextState: State = quoteManager.rotateFeaturedQuote(initialState)
    val featured: Option[Quote] = quoteManager.featuredQuote(nextState)
    assert(nextState.history.length == 3, s"Expecting next state's history to have 3 quotes, however next state's history length is [${nextState.history.length}]")
    assert(featured.isDefined, s"Expected next state's featured quote to exist but it is empty")
    assert(featured.get.quote == nextState.history(2).quote, s"Expected next state's featured quote to be [${nextState.history(2).quote}] but it was actually [${featured.get.quote}]")
  }

  it should "remove oldest last quote from history when rotating if history limit has been reached" in {
    val quotes: Seq[Quote] = parser.parse(rawQuotes) match {
      case Left(e) => assert(false, e); Nil
      case Right(qs) => qs
    }
    val history: Seq[Quote] = Seq(quotes(0), quotes(1), quotes(2))
    val initialState = State(quotes, history, historyLimit, RNG.RandomSeed(seed))
    val nextState: State = quoteManager.rotateFeaturedQuote(initialState)
    val featured: Option[Quote] = quoteManager.featuredQuote(nextState)
    assert(initialState.historyLimit == 3, s"Expected initial state's historyLimit to be 3, but it was actually [${initialState.historyLimit}]")
    assert(history.length == 3, s"Expected initial history to be 3, but it was actually [${history.length}]")
    assert(nextState.history.length == 3, s"Expected next state's history to have 3 quotes, however next state's history length is [${nextState.history.length}]")
    assert(featured.isDefined, s"Expected next state's featured quote to exist but it is empty")
    assert(featured.get.quote == nextState.history(2).quote, s"Expected next state's featured quote to be [${nextState.history(2).quote}] but it was actually [${featured.get.quote}]")
    assert(!nextState.history.contains(history.head), s"Expected next state's history to NOT contain the oldest quote, but it does")
  }

}