package com.iamouakil.muslimalarm.data.alarm
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String = "",
    val hour: Int = 7,
    val minute: Int = 0,
    val days: List<Int> = emptyList(),
    val isEnabled: Boolean = true,
    val isSleepAlarm: Boolean = false,
    val linkedPrayer: String? = null,
    val linkedPrayerOffsetMinutes: Int = 0,
    val progressiveWakeup: Boolean = false,
    val challenge: String = "none",
    val sound: String = "default",
    val vibration: Boolean = true,
    val skipNextOccurrence: Boolean = false,
    val snoozeMinutes: Int = 10
)
