package com.iamouakil.muslimalarm.data.hijri

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.chrono.HijrahChronology
import java.time.format.DateTimeFormatter
import java.util.Locale

object HijriDate {
    fun getTodayHijriString(): String {
        val today = LocalDate.now()
        val hijriDate = HijrahDate.from(today)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ar"))
        return "اليوم ${hijriDate.format(formatter)}"
    }
}
