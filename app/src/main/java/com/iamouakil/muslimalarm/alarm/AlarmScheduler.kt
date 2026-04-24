package com.iamouakil.muslimalarm.alarm
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iamouakil.muslimalarm.data.alarm.Alarm
import com.iamouakil.muslimalarm.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AlarmScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    fun schedule(alarm: Alarm, prayerTimeInMillis: Long? = null) {
        cancel(alarm)
        if (!alarm.isEnabled) return
        val targetTime = prayerTimeInMillis ?: calculateNextTime(alarm.hour, alarm.minute, alarm.days, alarm.skipNextOccurrence)
        setAlarmClock(alarm.id * 100, targetTime, alarm.id, "main")
        if (alarm.progressiveWakeup) {
            val light = targetTime - (10 * 60 * 1000)
            if (light > System.currentTimeMillis()) setAlarmClock(alarm.id * 100 + 1, light, alarm.id, "progressive_light")
            val medium = targetTime - (4 * 60 * 1000)
            if (medium > System.currentTimeMillis()) setAlarmClock(alarm.id * 100 + 2, medium, alarm.id, "progressive_medium")
        }
    }
    private fun setAlarmClock(requestCode: Int, timeInMillis: Long, alarmId: Int, type: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_TYPE", type)
        }
        val pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(timeInMillis, pi), pi)
    }
    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        listOf(alarm.id * 100, alarm.id * 100 + 1, alarm.id * 100 + 2).forEach { code ->
            val pi = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            alarmManager.cancel(pi)
            pi.cancel()
        }
    }
    private fun calculateNextTime(hour: Int, minute: Int, days: List<Int>, skip: Boolean): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        if (days.isNotEmpty()) while (!days.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) cal.add(Calendar.DAY_OF_YEAR, 1)
        if (skip) { cal.add(Calendar.DAY_OF_YEAR, 1); if (days.isNotEmpty()) while (!days.contains(cal.get(Calendar.DAY_OF_WEEK) - 1)) cal.add(Calendar.DAY_OF_YEAR, 1) }
        return cal.timeInMillis
    }
}
