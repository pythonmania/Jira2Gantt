package models

import java.text.ParseException
import java.util.Calendar
import java.text.SimpleDateFormat;
import java.util.Locale
import java.util.Date

object DateUtil {
  val sdfIn = new SimpleDateFormat(
    "EEE, dd MMM yyyy HH:mm:ss", Locale.US);
  val sdfOut = new SimpleDateFormat("M/d/yyyy", Locale.US);

  def getToday() = {
    sdfOut.format(new Date)
  }

  def convertDateFormat(dateStr: String) = {
    try {
      sdfOut.format(sdfIn.parse(dateStr));
    } catch {
      case e: ParseException =>
        getToday
    }
  }
}
