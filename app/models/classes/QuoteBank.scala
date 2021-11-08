package models.classes

import models.utilities.RNG

case class QuoteBank(
  featured: Option[Quote],
  quotes: Seq[Quote],
  history: Seq[Quote],
  historyLimit: Int,
  rng: RNG) {
    require(historyLimit <= quotes.length && historyLimit >= 0)
}

