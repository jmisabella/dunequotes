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
    val original = "2021-11-22 03"
    val expected = "2021-11-22 15"
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.after(original, format, 12, Calendar.HOUR_OF_DAY)
    assert(result == expected, s"Expected [$expected], actual [$result]")
  }

  it should "know when to daily rollover 24 hours after 2021-11-22 01:00" in {
    val originalTime = "2021-11-22 01:00"
    val nextTime = "2021-11-23 01:00"
    val expected = true 
    val format = "yyyy-MM-dd HH:mm"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 1 day after 2018/08/31" in {
    val originalTime = "2018/08/31"
    val nextTime = "2018/09/01"
    val expected = true 
    val format = "yyyy/MM/dd"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 2 days after 2018-08-31" in {
    val originalTime = "2018-08-31"
    val nextTime = "2018-09-02"
    val expected = true 
    val format = "yyyy-MM-dd"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 3 days after 2021-10-31 12:00:00" in {
    val originalTime = "2021-10-31 12:00:00"
    val nextTime = "2021-11-03 12:00:00"
    val expected = true 
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 4 days after 1980/11/03T12:31:02.233" in {
    val originalTime = "1980/11/03T12:31:02.233"
    val nextTime = "1980/11/07T12:31:02.233"
    val expected = true 
    val format = "yyyy/MM/dd'T'HH:mm:ss.SSS"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 365 days after 2021-02-14" in {
    val originalTime = "2021-02-14"
    val nextTime = "2022-02-14"
    val expected = true 
    val format = "yyyy-MM-dd"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when NOT to daily rollover 0 days after 2021-10-07" in {
    val originalTime = "2021-10-07"
    val nextTime = "2021-10-07"
    val expected = false
    val format = "yyyy-MM-dd"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when NOT to daily rollover 1 hour after 2020-03-14 07:30:00" in {
    val originalTime = "2020-03-14 07:30:00"
    val nextTime = "2020-03-14 08:30:00"
    val expected = false
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when NOT to daily rollover 23 hours after 2017/07/04T11:30" in {
    val originalTime = "2017/07/04T11:30"
    val nextTime = "2017/07/05T10:30"
    val expected = false
    val format = "yyyy/MM/dd'T'HH:mm"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when NOT to daily rollover 12 hours before 2021-10-01 00:00:00" in {
    val originalTime = "2021-10-01 00:00:00"
    val nextTime = "2021-09-31 12:00:00"
    val expected = false
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when NOT to daily rollover 1 day before 2021/10/07" in {
    val originalTime = "2021/10/07"
    val nextTime = "2021/10/06"
    val expected = false
    val format = "yyyy/MM/dd"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to hourly rollover 1 hours after 2021/10/01T06:30" in {
    val originalTime = "2021/10/01T06:30"
    val nextTime = "2021/10/01T07:30"
    val expected = true
    val format = "yyyy/MM/dd'T'HH:mm"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when to hourly rollover 60 minutes after 2021/10/01 07:45:20.124" in {
    val originalTime = "2021/10/01 07:45:20.124"
    val nextTime = "2021/10/01 08:45:20.124"
    val expected = true
    val format = "yyyy/MM/dd HH:mm:ss.SSS"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when to hourly rollover 7 hours after 2002-07-01 03" in {
    val originalTime = "2002-07-01 03"
    val nextTime = "2002-07-01 10"
    val expected = true
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to hourly rollover 1 day after 2018/08/31T11" in {
    val originalTime = "2018/08/31T11"
    val nextTime = "2018/09/01T11"
    val expected = true
    val format = "yyyy/MM/dd'T'HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when to hourly rollover 60 minutes after 2021-10-07 05" in {
    val originalTime = "2021-10-07 05"
    val nextTime = "2021-10-08 05"
    val expected = true
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }
  
  it should "know when NOT to hourly rollover 0 hours after 2021/10/07 00:30" in {
    val originalTime = "2021/10/07 00:30"
    val nextTime = "2021/10/07 00:30"
    val expected = false
    val format = "yyyy/MM/dd HH:mm"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when to hourly rollover 59 minutes after 2021-10-07 00" in {
    val originalTime = "2021-10-07 00"
    val nextTime = "2021-10-07 59"
    val expected = true
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.HOUR_OF_DAY)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when NOT to daily rollover 59 minutes after 2021-10-07 01" in {
    val originalTime = "2021-10-07 01"
    val nextTime = "2021-10-08 00"
    val expected = false
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to daily rollover 60 minutes after 2021-10-07 01" in {
    val originalTime = "2021-10-07 01"
    val nextTime = "2021-10-08 01"
    val expected = true
    val format = "yyyy-MM-dd HH"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.DATE)}]")
  }

  it should "know when to minute rollover 1 minute after 2021-11-27 17:00:00" in {
    val originalTime = "2021-11-27 17:00:00"
    val nextTime = "2021-11-27 17:01:00"
    val expected = true
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.MINUTE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.MINUTE)}]")
  }

  it should "know when to minute rollover 4 minutes after 2021-11-27 17:00:00" in {
    val originalTime = "2021-11-27 17:00:00"
    val nextTime = "2021-11-27 17:04:00"
    val expected = true
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.MINUTE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 4, Calendar.MINUTE)}]")
  }

  it should "know when to minute rollover 1 hour after 2021-11-27 17:00:00" in {
    val originalTime = "2021-11-27 17:00:00"
    val nextTime = "2021-11-27 18:00:00"
    val expected = true
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.MINUTE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 1, Calendar.HOUR_OF_DAY)}]")
  }

  it should "know when NOT to minute rollover 30 seconds after 2021-11-27 17:00:00" in {
    val originalTime = "2021-11-27 17:00:00"
    val nextTime = "2021-11-27 17:00:30"
    val expected = false
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.MINUTE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; " + 
      s"expected next [$nextTime], actual next [${timeRollover.after(originalTime, format, 30, Calendar.SECOND)}]")
  }

  it should "know when to minute rollover when previous time is missing (empty) from history and new time is 2021-11-27 17:01:00" in {
    val originalTime: String = ""
    val nextTime = "2021-11-27 17:01:00"
    val expected = true
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.MINUTE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; ")
  }

  it should "know when to daily rollover when previous time is missing (null) from history and new time is 2021-11-27 17:01:00" in {
    val originalTime: String = null 
    val nextTime = "2021-11-27 17:01:00"
    val expected = true
    val format = "yyyy-MM-dd HH:mm:ss"
    val result = timeRollover.isRollover(originalTime, nextTime, format, Calendar.DATE)
    assert(
      result == expected, 
      s"Expected result [$expected], actual result [$result]; ")
  }


}