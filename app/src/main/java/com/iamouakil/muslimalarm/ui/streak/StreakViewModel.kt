package com.iamouakil.muslimalarm.ui.streak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamouakil.muslimalarm.data.alarm.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreakViewModel @Inject constructor(
    private val streakPreferences: StreakPreferences,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    val currentStreak: StateFlow<String> = streakPreferences.getCurrentStreak()
        .map { "$it يوم" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0 يوم")

    val bestStreak: StateFlow<String> = streakPreferences.getBestStreak()
        .map { "$it يوم" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0 يوم")

    val canLogExcuse: StateFlow<Boolean> = streakPreferences.canLogExcuse()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun recordWakeup() {
        viewModelScope.launch {
            streakPreferences.incrementStreak()
        }
    }

    fun logExcuse() {
        viewModelScope.launch {
            streakPreferences.logExcuse()
        }
    }
}
