package com.iamouakil.muslimalarm.ui.alarm
import android.app.KeyguardManager
import android.content.Context
import android.media.*
import android.os.*
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.iamouakil.muslimalarm.data.alarm.Alarm
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import com.iamouakil.muslimalarm.ui.theme.MuslimAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @Inject lateinit var alarmRepository: AlarmRepository
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private val currentAlarm = mutableStateOf<Alarm?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        showOverLockscreen()
        super.onCreate(savedInstanceState)
        val alarmId = intent.getIntExtra("EXTRA_ALARM_ID", -1)
        val alarmType = intent.getStringExtra("EXTRA_ALARM_TYPE") ?: "main"
        if (alarmId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val alarm = alarmRepository.getAlarmById(alarmId)
                withContext(Dispatchers.Main) {
                    currentAlarm.value = alarm
                    if (alarm != null) playAlarm(alarm)
                }
            }
        }
        setContent {
            MuslimAlarmTheme {
                val alarm = currentAlarm.value
                if (alarm != null) AlarmRingScreenShell(alarm, alarmType) { stopAndFinish() }
                else Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }
    private fun showOverLockscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true); setTurnScreenOn(true)
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    private fun playAlarm(alarm: Alarm) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@AlarmActivity, uri)
                setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                isLooping = true; prepare(); start()
            }
        } catch (e: Exception) { e.printStackTrace() }
        if (alarm.vibration) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            else @Suppress("DEPRECATION") getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 500, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            else @Suppress("DEPRECATION") vibrator?.vibrate(pattern, 0)
        }
    }
    private fun stopAndFinish() { mediaPlayer?.stop(); mediaPlayer?.release(); mediaPlayer = null; vibrator?.cancel(); vibrator = null; finish() }
    override fun onDestroy() { stopAndFinish(); super.onDestroy() }
}
@Composable
fun AlarmRingScreenShell(alarm: Alarm, type: String, onDismiss: () -> Unit) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Button(onClick = onDismiss) { Text("إيقاف المنبه") }
    }
}
