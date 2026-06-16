package com.iamouakil.muslimalarm.ui.streak

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("streak_prefs") }
    )

    companion object {
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val BEST_STREAK = intPreferencesKey("best_streak")
        val LAST_WAKEUP_DATE = stringPreferencesKey("last_wakeup_date")
        val EXCUSE_COUNT_THIS_WEEK = intPreferencesKey("excuse_count_this_week")
        val LAST_EXCUSE_DATE = stringPreferencesKey("last_excuse_date")
    }

    fun getCurrentStreak(): Flow<Int> = dataStore.data.map { it[CURRENT_STREAK] ?: 0 }
    
    fun getBestStreak(): Flow<Int> = dataStore.data.map { it[BEST_STREAK] ?: 0 }

    fun canLogExcuse(): Flow<Boolean> = dataStore.data.map { prefs ->
        val lastExcuseStr = prefs[LAST_EXCUSE_DATE] ?: ""
        val count = prefs[EXCUSE_COUNT_THIS_WEEK] ?: 0
        
        if (lastExcuseStr.isEmpty()) return@map true

        val lastExcuseDate = LocalDate.parse(lastExcuseStr)
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(lastExcuseDate, today)
        
        val currentWeek = today.get(WeekFields.ISO.weekOfWeekBasedYear())
        val lastWeek = lastExcuseDate.get(WeekFields.ISO.weekOfWeekBasedYear())

        val isNewWeek = daysBetween >= 7 || currentWeek != lastWeek || today.year != lastExcuseDate.year
        if (isNewWeek) {
            true 
        } else {
            count == 0 
        }
    }

    suspend fun logExcuse() {
        dataStore.edit { prefs ->
            val lastExcuseStr = prefs[LAST_EXCUSE_DATE] ?: ""
            val today = LocalDate.now()
            
            var count = prefs[EXCUSE_COUNT_THIS_WEEK] ?: 0

            if (lastExcuseStr.isNotEmpty()) {
                val lastExcuseDate = LocalDate.parse(lastExcuseStr)
                val daysBetween = ChronoUnit.DAYS.between(lastExcuseDate, today)
                val currentWeek = today.get(WeekFields.ISO.weekOfWeekBasedYear())
                val lastWeek = lastExcuseDate.get(WeekFields.ISO.weekOfWeekBasedYear())

                if (daysBetween >= 7 || currentWeek != lastWeek || today.year != lastExcuseDate.year) {
                    count = 0
                }
            }

            prefs[EXCUSE_COUNT_THIS_WEEK] = count + 1
            prefs[LAST_EXCUSE_DATE] = today.toString()
            prefs[LAST_WAKEUP_DATE] = today.toString()
        }
    }

    suspend fun incrementStreak() {
        dataStore.edit { prefs ->
            val today = LocalDate.now().toString()
            val lastDateStr = prefs[LAST_WAKEUP_DATE] ?: ""
            
            if (lastDateStr == today) {
                return@edit
            }

            var current = prefs[CURRENT_STREAK] ?: 0
            val best = prefs[BEST_STREAK] ?: 0

            if (lastDateStr.isNotEmpty()) {
                val lastDate = LocalDate.parse(lastDateStr)
                val days = ChronoUnit.DAYS.between(lastDate, LocalDate.now())
                
                if (days == 1L) {
                    current += 1
                } else {
                    current = 1
                }
            } else {
                current = 1
            }

            prefs[CURRENT_STREAK] = current
            if (current > best) {
                prefs[BEST_STREAK] = current
            }
            prefs[LAST_WAKEUP_DATE] = today
        }
    }
}
