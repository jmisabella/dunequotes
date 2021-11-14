package models.classes

import models.utilities.RNG

// represents the current state of the application
case class QuoteBank(
  quotes: Seq[Quote],  // total repository of quotes
  history: Seq[Quote], // history of quotes which have been featured 
  historyLimit: Int,   // size of featured quote history
  rng: RNG,            // used when rotating featured quote
  currentDate: String = "") { // currentDate is used to determine when featured quote should be rotated
    require(historyLimit <= quotes.length && historyLimit >= 0)
}

