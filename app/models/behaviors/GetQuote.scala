package models.behaviors

import models.classes.{ Quote, QuoteBank }
import models.utilities.RNG

trait GetQuote {

  // TODO: test
  def randomQuote(state: QuoteBank): (Quote, QuoteBank) = {
    val (random, nextSeed): (Int, RNG) = state.rng.boundedPositiveInt(state.quotes.length + 1)
    val quote: Quote = state.quotes(random)
    val stateWithUpdatedSeed: QuoteBank = state.copy(rng = nextSeed)
    (quote, stateWithUpdatedSeed)
  }

  def featuredQuote(state: QuoteBank): Option[Quote] = state.history.reverse.headOption

  def rotateFeaturedQuote(state: QuoteBank): QuoteBank = {
    val remaining = state.quotes.diff(state.history)
    val (index, nextSeed): (Int, RNG) = state.rng.boundedPositiveInt(state.quotes.length + 1)
    val quote = remaining(index % remaining.length)
    var newHistory = if (state.history == Nil) Seq(quote) else state.history ++ Seq(quote)
    newHistory = if (newHistory.length > state.historyLimit) newHistory.tail else newHistory 
    state.copy(history = newHistory)
  }
}