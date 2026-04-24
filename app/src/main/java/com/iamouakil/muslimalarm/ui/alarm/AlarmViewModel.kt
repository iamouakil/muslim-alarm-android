package com.iamouakil.muslimalarm.ui.alarm
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamouakil.muslimalarm.alarm.AlarmScheduler
import com.iamouakil.muslimalarm.data.alarm.Alarm
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {
    val alarms: StateFlow<List<Alarm>> = repository.getAllAlarms().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    fun addAlarm(alarm: Alarm) { viewModelScope.launch { val id = repository.insert(alarm); scheduler.schedule(alarm.copy(id = id.toInt())) } }
    fun updateAlarm(alarm: Alarm) { viewModelScope.launch { repository.update(alarm); if (alarm.isEnabled) scheduler.schedule(alarm) else scheduler.cancel(alarm) } }
    fun deleteAlarm(alarm: Alarm) { viewModelScope.launch { scheduler.cancel(alarm); repository.delete(alarm) } }
    fun toggleAlarm(alarm: Alarm) { updateAlarm(alarm.copy(isEnabled = !alarm.isEnabled)) }
    fun skipNextOccurrence(alarm: Alarm) { updateAlarm(alarm.copy(skipNextOccurrence = true)) }
    fun scheduleAll() { viewModelScope.launch { repository.getEnabledAlarms().forEach { scheduler.schedule(it) } } }
}
