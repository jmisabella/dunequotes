package models.behaviors

import models.classes.{ Quote, QuoteBank }
import models.utilities.RNG

trait GetQuote {

  // TODO: test
  def randomQuote(quotes: QuoteBank): (Quote, QuoteBank) = {
    val (random, nextSeed): (Int, RNG) = quotes.rng.boundedPositiveInt(quotes.quotes.length + 1)
    val quote: Quote = quotes.quotes(random)
    val quotesWithUpdatedSeed: QuoteBank = quotes.copy(rng = nextSeed)
    (quote, quotesWithUpdatedSeed)
  }

  // TODO: define featuredQuote and rotateFeaturedQuote
}