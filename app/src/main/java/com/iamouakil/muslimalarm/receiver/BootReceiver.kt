package com.iamouakil.muslimalarm.receiver
import android.content.*
import com.iamouakil.muslimalarm.alarm.AlarmScheduler
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var alarmRepository: AlarmRepository
    @Inject lateinit var alarmScheduler: AlarmScheduler
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val pr = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try { alarmRepository.getEnabledAlarms().forEach { alarmScheduler.schedule(it) } }
                finally { pr.finish() }
            }
        }
    }
}
