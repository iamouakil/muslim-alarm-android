package com.iamouakil.muslimalarm.receiver
import android.app.*
import android.content.*
import android.os.Build
import androidx.core.app.NotificationCompat
import com.iamouakil.muslimalarm.alarm.AlarmScheduler
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import com.iamouakil.muslimalarm.ui.alarm.AlarmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("EXTRA_ALARM_ID", -1)
        val type = intent.getStringExtra("EXTRA_ALARM_TYPE") ?: "main"
        if (alarmId == -1) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (type == "main") {
                    val alarm = alarmRepository.getAlarmById(alarmId)
                    if (alarm != null) {
                        when {
                            alarm.days.isEmpty() -> alarmRepository.update(alarm.copy(isEnabled = false))
                            alarm.skipNextOccurrence -> { val updated = alarm.copy(skipNextOccurrence = false); alarmRepository.update(updated); alarmScheduler.schedule(updated) }
                            else -> alarmScheduler.schedule(alarm)
                        }
                    }
                }
                launchAlarmUI(context, alarmId, type)
            } finally { pendingResult.finish() }
        }
    }
    private fun launchAlarmUI(context: Context, alarmId: Int, type: String) {
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId); putExtra("EXTRA_ALARM_TYPE", type)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(context, alarmId * 100, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "muslim_alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, "Alarm Ringing", NotificationManager.IMPORTANCE_HIGH).apply { setBypassDnd(true) }
            nm.createNotificationChannel(ch)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("منبه المسلم").setContentText("حان وقت الاستيقاظ!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pi, true).setAutoCancel(true).build()
        nm.notify(alarmId, notification)
    }
}
