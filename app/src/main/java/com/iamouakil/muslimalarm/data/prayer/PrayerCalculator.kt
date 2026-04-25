package com.iamouakil.muslimalarm.data.prayer

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.DateComponents
import com.batoulapps.adhan.data.DateComponents
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import java.time.LocalDate
import java.time.ZoneId

enum class CalculationMethodEnum {
    MOROCCO, MUSLIM_WORLD_LEAGUE, EGYPTIAN, KARACHI, UMM_AL_QURA, KUWAIT, QATAR, NORTH_AMERICA
}

data class PrayerTimesResult(
    val fajr: Long,
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val maghrib: Long,
    val isha: Long,
    val date: LocalDate
)

object PrayerCalculator {

    fun calculate(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        method: CalculationMethodEnum,
        timezone: String = "Africa/Casablanca"
    ): PrayerTimesResult {
        val coordinates = Coordinates(latitude, longitude)
        val dateComponents = DateComponents(date.year, date.monthValue, date.dayOfMonth)

        val parameters: CalculationParameters = when (method) {
            CalculationMethodEnum.MOROCCO -> {
                val p = CalculationParameters(19.0, 17.0)
                p.adjustments.dhuhr = 5
                p.adjustments.maghrib = 5
                p
            }
            CalculationMethodEnum.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
            CalculationMethodEnum.EGYPTIAN -> CalculationMethod.EGYPTIAN.parameters
            CalculationMethodEnum.KARACHI -> CalculationMethod.KARACHI.parameters
            CalculationMethodEnum.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA.parameters
            CalculationMethodEnum.KUWAIT -> CalculationMethod.KUWAIT.parameters
            CalculationMethodEnum.QATAR -> CalculationMethod.QATAR.parameters
            CalculationMethodEnum.NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA.parameters
        }
        parameters.madhab = Madhab.SHAFI

        val pt = PrayerTimes(coordinates, dateComponents, parameters)

        return PrayerTimesResult(
            fajr = pt.fajr.time,
            sunrise = pt.sunrise.time,
            dhuhr = pt.dhuhr.time,
            asr = pt.asr.time,
            maghrib = pt.maghrib.time,
            isha = pt.isha.time,
            date = date
        )
    }

    fun nextPrayer(times: PrayerTimesResult, tomorrowTimes: PrayerTimesResult): Pair<String, Long> {
        val now = System.currentTimeMillis()
        return when {
            now < times.fajr -> "الفجر" to times.fajr
            now < times.sunrise -> "الشروق" to times.sunrise
            now < times.dhuhr -> "الظهر" to times.dhuhr
            now < times.asr -> "العصر" to times.asr
            now < times.maghrib -> "المغرب" to times.maghrib
            now < times.isha -> "العشاء" to times.isha
            else -> "الفجر" to tomorrowTimes.fajr
        }
    }

    fun lastThirdOfNight(times: PrayerTimesResult, tomorrowFajr: Long): Long {
        val duration = tomorrowFajr - times.maghrib
        return times.maghrib + (duration * (2.0 / 3.0)).toLong()
    }

    fun midnight(times: PrayerTimesResult, tomorrowFajr: Long): Long {
        return times.maghrib + ((tomorrowFajr - times.maghrib) / 2)
    }
}

