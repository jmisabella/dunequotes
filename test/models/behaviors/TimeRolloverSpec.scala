package models.behaviors

import models.behaviors.TimeRollover
import org.scalatest.flatspec.AnyFlatSpec
import java.util.Calendar

class TimeRolloverSpec extends AnyFlatSpec {
  case object timeRollover extends TimeRollover

  "TimeRollover" should "get one day after 2021-11-02" in {
    val original = "2021-11-02" 
    val expected = "2021-11-03"
    val format = "yyyy-MM-dd"
    val result = timeRollover.after(original, format)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get two days after 2021-11-02T07" in {
    val original = "2021-11-02T07" 
    val expected = "2021-11-04T07"
    val format = "yyyy-MM-dd'T'HH"
    val result = timeRollover.after(original, format, 2)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get zero days after 2021/11/02" in {
    val original = "2021/11/02" 
    val expected = "2021/11/02"
    val format = "yyyy/MM/dd"
    val result = timeRollover.after(original, format, 0)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get zero days before 1986-01-01" in {
    val original = "1986-01-01" 
    val expected = "1986-01-01"
    val format = "yyyy-MM-dd"
    val result = timeRollover.before(original, format, 0)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get one day before 1986/01/01" in {
    val original = "1986/01/01" 
    val expected = "1985/12/31"
    val format = "yyyy/MM/dd"
    val result = timeRollover.before(original, format, 1)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get 365 days before 2021-11-22 12:35:05.021" in {
    val original = "2021-11-22 12:35:05.021" 
    val expected = "2020-11-22 12:35:05.021"
    val format = "yyyy-MM-dd HH:mm:ss.SSS"
    val result = timeRollover.before(original, format, 365)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get 1 year before 2021-11-22 12:35:05.021" in {
    val original = "2021-11-22 12:35:05.021" 
    val expected = "2020-11-22 12:35:05.021"
    val format = "yyyy-MM-dd HH:mm:ss.SSS"
    val result = timeRollover.before(original, format, 1, Calendar.YEAR)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }
  
  it should "get 366 days before 2021/11/22" in {
    val original = "2021/11/22" 
    val expected = "2020/11/21"
    val format = "yyyy/MM/dd"
    val result = timeRollover.before(original, format, 366)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get zero hours after 2021/12/31 23:15" in {
    val original = "2021/12/31 23:15"
    val expected = "2021/12/31 23:15"
    val format = "yyyy/MM/dd HH:mm"
    val result = timeRollover.after(original, format, 0, Calendar.HOUR_OF_DAY)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get 1 hour after 2021/12/31 23:15" in {
    val original = "2021/12/31 23:15"
    val expected = "2022/01/01 00:15"
    val format = "yyyy/MM/dd HH:mm"
    val result = timeRollover.after(original, format, 1, Calendar.HOUR_OF_DAY)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "get 12 hours after 2021-11-22 03" in {
    val original = "2021-12-31 03"
    val expected = "2021-11-22 15"
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.after(original, format, 12, Calendar.HOUR_OF_DAY)
  }

  // it should "know when to rollover by date" in {
  //   ???
  // }

  // it should "know when to rollover by hour" in {
  //   ???
  // }

  // it should "know when not to rollover by date" in {
  //   ???
  // }
}
