package models.classes

import models.utilities.RNG
import scala.util.Random
import java.util.Calendar

// represents the current state of the application
case class QuoteBank(
  quotes: Seq[Quote],  // total repository of quotes
  history: Seq[Quote], // history of quotes which have been featured 
  historyLimit: Int,   // size of featured quote history
  rng: RNG,            // used when rotating featured quote
  rolloverTime: String = "",
  rolloverTimeIntervalUnit: Int = Calendar.DATE) { // rolloverTime is used to determine when featured quote should be rotated
    require(historyLimit <= quotes.length && historyLimit >= 0)
}
object QuoteBank {
  def apply(): QuoteBank = QuoteBank(Nil, Nil, 0, RNG.RandomSeed(Random.nextInt()), "", Calendar.DATE)
}
