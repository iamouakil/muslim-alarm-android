package com.iamouakil.muslimalarm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.prayerDataStore: DataStore<Preferences> by preferencesDataStore(name = "prayer_prefs")

@Singleton
class PrayerPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val SELECTED_CITY_ID = intPreferencesKey("selected_city_id")
    private val SELECTED_CITY_NAME = stringPreferencesKey("selected_city_name")
    private val SELECTED_CITY_LAT = doublePreferencesKey("selected_city_lat")
    private val SELECTED_CITY_LNG = doublePreferencesKey("selected_city_lng")
    private val SELECTED_CITY_TIMEZONE = stringPreferencesKey("selected_city_timezone")
    private val CALCULATION_METHOD = stringPreferencesKey("calculation_method")
    private val MANUAL_OFFSETS = stringPreferencesKey("manual_offsets")
    private val USE_GPS = booleanPreferencesKey("use_gps")

    val preferencesFlow: Flow<PrayerPrefsData> = context.prayerDataStore.data.map { prefs ->
        PrayerPrefsData(
            selectedCityId = prefs[SELECTED_CITY_ID] ?: -1,
            selectedCityName = prefs[SELECTED_CITY_NAME] ?: "",
            selectedCityLat = prefs[SELECTED_CITY_LAT] ?: 34.0209,
            selectedCityLng = prefs[SELECTED_CITY_LNG] ?: -6.8416,
            selectedCityTimezone = prefs[SELECTED_CITY_TIMEZONE] ?: "Africa/Casablanca",
            calculationMethod = prefs[CALCULATION_METHOD] ?: "MOROCCO",
            manualOffsets = prefs[MANUAL_OFFSETS] ?: "fajr:0,dhuhr:0,asr:0,maghrib:0,isha:0",
            useGps = prefs[USE_GPS] ?: true
        )
    }

    suspend fun updateCity(id: Int, name: String, lat: Double, lng: Double, timezone: String) {
        context.prayerDataStore.edit { prefs ->
            prefs[SELECTED_CITY_ID] = id
            prefs[SELECTED_CITY_NAME] = name
            prefs[SELECTED_CITY_LAT] = lat
            prefs[SELECTED_CITY_LNG] = lng
            prefs[SELECTED_CITY_TIMEZONE] = timezone
            prefs[USE_GPS] = false
        }
    }

    suspend fun setUseGps(useGps: Boolean) {
        context.prayerDataStore.edit { prefs -> prefs[USE_GPS] = useGps }
    }

    suspend fun setCalculationMethod(method: String) {
        context.prayerDataStore.edit { prefs -> prefs[CALCULATION_METHOD] = method }
    }
}

data class PrayerPrefsData(
    val selectedCityId: Int,
    val selectedCityName: String,
    val selectedCityLat: Double,
    val selectedCityLng: Double,
    val selectedCityTimezone: String,
    val calculationMethod: String,
    val manualOffsets: String,
    val useGps: Boolean
)
