import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.time.temporal.{ChronoUnit, TemporalAdjusters}
import java.util.Locale

trait OffsetProvider {
  def offset: (Int, Long)

  def suffix: String = {
    val (year, count) = offset
    val lastDigits = year - 2000
    s"$lastDigits-$count"
  }
}

object MinutesOffset extends OffsetProvider {
  override def offset: (Int, Long) = {
    val now = LocalDateTime.now()
    val year = now.getYear
    val fdoy = LocalDate.now().`with`(TemporalAdjusters.firstDayOfYear()).atStartOfDay()
    val minutes = ChronoUnit.MINUTES.between(fdoy, now)
    (year, minutes)
  }
}

object DaysOffset extends OffsetProvider {
  override def offset: (Int, Long) = {
    val now = LocalDateTime.now()
    val day = now.getDayOfYear
    val year = now.getYear
    (year, day)
  }
}

object ReadableDateOffset extends OffsetProvider {

  override def suffix: String = {
    val now = LocalDateTime.now()
    //We want to have DateTime encoding with minutes precision:
    //17-Sept-26T14-33
    now.format(DateTimeFormatter.ofPattern("yy-MMM-dd'T'HH-mm", Locale.US)).toLowerCase //todo remove lowerCase when Anil fixes CBS bug:
  }

  override def offset: (Int, Long) = (0, 0L)
}

object SQLDateOffset extends OffsetProvider {

  override def suffix: String = {
    val now = LocalDateTime.now()
    //We want to have DateTime encoding with minutes precision:
    //17-Sept-26T14-33
    now.format(DateTimeFormatter.ofPattern("yy_MMM_dd__HH_mm", Locale.US)).toLowerCase
  }

  override def offset: (Int, Long) = (0, 0L)
}
