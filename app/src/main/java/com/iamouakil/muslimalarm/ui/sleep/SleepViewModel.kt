package com.iamouakil.muslimalarm.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamouakil.muslimalarm.alarm.AlarmScheduler
import com.iamouakil.muslimalarm.data.alarm.Alarm
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import com.iamouakil.muslimalarm.data.prayer.PrayerCalculator
import com.iamouakil.muslimalarm.data.prayer.PrayerTimesResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SleepCycleOption(
    val cycleCount: Int,
    val wakeTime: LocalTime,
    val qualityLabelAr: String
)

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val sleepPreferences: SleepPreferences,
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val prayerCalculator: PrayerCalculator
) : ViewModel() {

    val bedtimeHour = sleepPreferences.bedtimeHour.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 22)
    val bedtimeMinute = sleepPreferences.bedtimeMinute.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 30)
    val wakeupHour = sleepPreferences.wakeupHour.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)
    val wakeupMinute = sleepPreferences.wakeupMinute.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _sleepCycleOptions = MutableStateFlow<List<SleepCycleOption>>(emptyList())
    val sleepCycleOptions: StateFlow<List<SleepCycleOption>> = _sleepCycleOptions.asStateFlow()

    private val _napWakeTime = MutableStateFlow("")
    val napWakeTime: StateFlow<String> = _napWakeTime.asStateFlow()

    private val _caffeineClearanceTime = MutableStateFlow("")
    val caffeineClearanceTime: StateFlow<String> = _caffeineClearanceTime.asStateFlow()

    private val _suggestedBedtime = MutableStateFlow("")
    val suggestedBedtime: StateFlow<String> = _suggestedBedtime.asStateFlow()

    private val _qiyamTimeText = MutableStateFlow("")
    val qiyamTimeText: StateFlow<String> = _qiyamTimeText.asStateFlow()

    private val _qiyamCountdown = MutableStateFlow("")
    val qiyamCountdown: StateFlow<String> = _qiyamCountdown.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var suggestedBedtimeLocal: LocalTime? = null
    private var qiyamDateTime: LocalDateTime? = null
    private var qiyamJob: Job? = null

    fun saveSleepPlan(bedH: Int, bedM: Int, wakeH: Int, wakeM: Int) {
        viewModelScope.launch {
            sleepPreferences.setBedtime(bedH, bedM)
            sleepPreferences.setWakeup(wakeH, wakeM)
            
            val alarm = Alarm(
                label = "استيقاظ النوم",
                hour = wakeH,
                minute = wakeM,
                days = emptyList(),
                isSleepAlarm = true,
                challenge = "none",
                progressiveWakeup = false
            )
            alarmRepository.insert(alarm)
            alarmScheduler.schedule(alarm)
        }
    }

    fun calculateSleepCycles(bedH: Int, bedM: Int) {
        val baseTime = LocalTime.of(bedH, bedM).plusMinutes(15)
        val options = listOf(
            SleepCycleOption(4, baseTime.plusMinutes(4 * 90L), "كافٍ"),
            SleepCycleOption(5, baseTime.plusMinutes(5 * 90L), "جيد"),
            SleepCycleOption(6, baseTime.plusMinutes(6 * 90L), "جيد جداً"),
            SleepCycleOption(7, baseTime.plusMinutes(7 * 90L), "ممتاز")
        )
        _sleepCycleOptions.value = options
    }

    fun scheduleCycleAlarm(option: SleepCycleOption) {
        viewModelScope.launch {
            val alarm = Alarm(
                label = "استيقاظ دورة ${option.cycleCount}",
                hour = option.wakeTime.hour,
                minute = option.wakeTime.minute,
                days = emptyList(),
                isSleepAlarm = false,
                challenge = "none",
                progressiveWakeup = false
            )
            alarmRepository.insert(alarm)
            alarmScheduler.schedule(alarm)
        }
    }

    fun scheduleNap(durationMin: Int) {
        viewModelScope.launch {
            val wakeTime = LocalTime.now().plusMinutes(durationMin.toLong())
            _napWakeTime.value = wakeTime.format(timeFormatter)

            val alarm = Alarm(
                label = "قيلولة $durationMin دقيقة",
                hour = wakeTime.hour,
                minute = wakeTime.minute,
                days = emptyList(),
                isSleepAlarm = false,
                challenge = "none",
                progressiveWakeup = false
            )
            alarmRepository.insert(alarm)
            alarmScheduler.schedule(alarm)
        }
    }

    fun calculateCaffeine(hour: Int, min: Int) {
        val intakeTime = LocalTime.of(hour, min)
        val clearance = intakeTime.plusHours(10)
        val suggested = intakeTime.plusHours(11)
        
        suggestedBedtimeLocal = suggested
        _caffeineClearanceTime.value = clearance.format(timeFormatter)
        _suggestedBedtime.value = suggested.format(timeFormatter)
    }

    fun scheduleCaffeineSleepAlarm() {
        suggestedBedtimeLocal?.let { time ->
            viewModelScope.launch {
                val alarm = Alarm(
                    label = "موعد النوم بعد الكافيين",
                    hour = time.hour,
                    minute = time.minute,
                    days = emptyList(),
                    isSleepAlarm = false,
                    challenge = "none",
                    progressiveWakeup = false
                )
                alarmRepository.insert(alarm)
                alarmScheduler.schedule(alarm)
            }
        }
    }

    fun setupQiyam() {
        qiyamJob?.cancel()
        qiyamJob = viewModelScope.launch {
            val now = LocalDateTime.now()
            val today = now.toLocalDate()
            
            val todayTimes = prayerCalculator.calculate(
                today, 33.5731, -7.5898, com.iamouakil.muslimalarm.data.prayer.CalculationMethodEnum.MOROCCO
            )
            val tomorrowTimes = prayerCalculator.calculate(
                today.plusDays(1), 33.5731, -7.5898, com.iamouakil.muslimalarm.data.prayer.CalculationMethodEnum.MOROCCO
            )
            
            val qiyamMillis = prayerCalculator.lastThirdOfNight(todayTimes, tomorrowTimes.fajr)
            val targetDateTime = Instant.ofEpochMilli(qiyamMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                
            qiyamDateTime = targetDateTime
            _qiyamTimeText.value = targetDateTime.format(timeFormatter)

            while (isActive) {
                val nowLoop = LocalDateTime.now()
                val duration = java.time.Duration.between(nowLoop, targetDateTime)
                
                if (duration.isNegative) {
                    _qiyamCountdown.value = "00:00:00"
                    break
                }
                
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                val seconds = duration.seconds % 60
                _qiyamCountdown.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000)
            }
        }
    }

    fun scheduleQiyamAlarm() {
        qiyamDateTime?.let { time ->
            viewModelScope.launch {
                val alarm = Alarm(
                    label = "قيام الليل",
                    hour = time.hour,
                    minute = time.minute,
                    days = emptyList(),
                    isSleepAlarm = false,
                    challenge = "none",
                    progressiveWakeup = false
                )
                alarmRepository.insert(alarm)
                alarmScheduler.schedule(alarm)
            }
        }
    }
}
