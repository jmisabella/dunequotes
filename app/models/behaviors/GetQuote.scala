package models.behaviors

import models.classes.{ Quote, State }
import models.utilities.RNG

trait GetQuote {

  // TODO: test
  def randomQuote(state: State): (State, Quote) = {
    val (random, nextSeed): (Int, RNG) = state.rng.boundedPositiveInt(state.quotes.length + 1)
    val quote: Quote = state.quotes(random)
    val stateWithUpdatedSeed: State = state.copy(rng = nextSeed)
    (stateWithUpdatedSeed, quote)
  }

  def featuredQuote(state: State): Option[Quote] = state.history.reverse.headOption

  def rotateFeaturedQuote(state: State): State = {
    val remaining = state.quotes.diff(state.history)
    val (index, nextSeed): (Int, RNG) = state.rng.boundedPositiveInt(state.quotes.length + 1)
    val quote = remaining(index % remaining.length)
    var newHistory = if (state.history == Nil) Seq(quote) else state.history ++ Seq(quote)
    newHistory = if (newHistory.length > state.historyLimit) newHistory.tail else newHistory 
    state.copy(history = newHistory)
  }
}