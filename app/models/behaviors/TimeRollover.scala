package models.behaviors

import java.text.SimpleDateFormat
import java.util.Calendar

trait TimeRollover {

  private def getByOffset(date: String, offset: Int, format: String = "yyyy-MM-dd", calendarUnitIdentifier: Int = Calendar.DATE): String = {
    val formatter = new SimpleDateFormat(format)
    val cal = Calendar.getInstance()
    cal.setTime(formatter.parse(date))
    cal.add(calendarUnitIdentifier, offset)
    formatter.format(cal.getTime())
  }

  def before(date: String, format: String = "yyyy-MM-dd", numberOfTimeUnitsBefore: Int = 1, calendarUnitIdentifier: Int = Calendar.DATE): String = {
    getByOffset(date, -numberOfTimeUnitsBefore.abs, format, calendarUnitIdentifier)
  }

  def after(date: String, format: String = "yyyy-MM-dd", numberOfTimeUnitsAfter: Int = 1, calendarUnitIdentifier: Int = Calendar.DATE): String = {
    getByOffset(date, numberOfTimeUnitsAfter.abs, format, calendarUnitIdentifier)
  }

  // TODO: testing is showing that this does not work as expected
  def isRollover(previousTime: String, nextTime: String, format: String, calendarUnitIdentifier: Int = Calendar.DATE): Boolean = {
    nextTime == after(previousTime, format, 1, calendarUnitIdentifier)
  }

}