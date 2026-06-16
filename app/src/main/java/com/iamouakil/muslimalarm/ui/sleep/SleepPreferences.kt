// FILE: com/iamouakil/muslimalarm/ui/sleep/SleepPreferences.kt
package com.iamouakil.muslimalarm.ui.sleep

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SleepPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("sleep_preferences") }
    )

    private val BEDTIME_HOUR = intPreferencesKey("bedtime_hour")
    private val BEDTIME_MINUTE = intPreferencesKey("bedtime_minute")
    private val WAKEUP_HOUR = intPreferencesKey("wakeup_hour")
    private val WAKEUP_MINUTE = intPreferencesKey("wakeup_minute")

    val bedtimeHour: Flow<Int> = dataStore.data.map { it[BEDTIME_HOUR] ?: 22 }
    val bedtimeMinute: Flow<Int> = dataStore.data.map { it[BEDTIME_MINUTE] ?: 30 }
    val wakeupHour: Flow<Int> = dataStore.data.map { it[WAKEUP_HOUR] ?: 6 }
    val wakeupMinute: Flow<Int> = dataStore.data.map { it[WAKEUP_MINUTE] ?: 0 }

    suspend fun setBedtime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[BEDTIME_HOUR] = hour
            preferences[BEDTIME_MINUTE] = minute
        }
    }

    suspend fun setWakeup(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[WAKEUP_HOUR] = hour
            preferences[WAKEUP_MINUTE] = minute
        }
    }
}
