package com.iamouakil.muslimalarm.ui.prayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iamouakil.muslimalarm.data.city.City
import com.iamouakil.muslimalarm.data.city.CityRepository
import com.iamouakil.muslimalarm.data.location.LocationRepository
import com.iamouakil.muslimalarm.data.prayer.CalculationMethodEnum
import com.iamouakil.muslimalarm.data.prayer.PrayerCalculator
import com.iamouakil.muslimalarm.data.prayer.PrayerTimesResult
import com.iamouakil.muslimalarm.data.preferences.PrayerPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val locationRepository: LocationRepository,
    private val preferences: PrayerPreferences
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<PrayerTimesResult?>(null)
    val prayerTimes: StateFlow<PrayerTimesResult?> = _prayerTimes.asStateFlow()

    private val _nextPrayer = MutableStateFlow<Pair<String, Long>?>(null)
    val nextPrayer: StateFlow<Pair<String, Long>?> = _nextPrayer.asStateFlow()

    private val _countdown = MutableStateFlow("00:00:00")
    val countdown: StateFlow<String> = _countdown.asStateFlow()

    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _isUsingGps = MutableStateFlow(true)
    val isUsingGps: StateFlow<Boolean> = _isUsingGps.asStateFlow()

    private val _citySearchResults = MutableStateFlow<List<City>>(emptyList())
    val citySearchResults: StateFlow<List<City>> = _citySearchResults.asStateFlow()

    private val _calculationMethod = MutableStateFlow(CalculationMethodEnum.MOROCCO)
    val calculationMethod: StateFlow<CalculationMethodEnum> = _calculationMethod.asStateFlow()

    private var searchJob: Job? = null
    private var tickerJob: Job? = null
    private var currentTimezone: String = "Africa/Casablanca"

    init {
        viewModelScope.launch { cityRepository.loadCitiesFromAssetsIfEmpty() }
        viewModelScope.launch {
            preferences.preferencesFlow.collectLatest { prefs ->
                _isUsingGps.value = prefs.useGps
                _calculationMethod.value = CalculationMethodEnum.valueOf(prefs.calculationMethod)
                currentTimezone = prefs.selectedCityTimezone
                if (prefs.useGps) {
                    _selectedCity.value = "الموقع الحالي"
                    fetchLocationAndCalculate(prefs.calculationMethod)
                } else {
                    _selectedCity.value = prefs.selectedCityName.ifEmpty { "غير محدد" }
                    calculatePrayers(prefs.selectedCityLat, prefs.selectedCityLng, prefs.selectedCityTimezone, prefs.calculationMethod)
                }
            }
        }
        startTicker()
    }

    private fun fetchLocationAndCalculate(methodName: String) {
        viewModelScope.launch {
            locationRepository.getCurrentLocation().collect { loc ->
                val lat = loc?.first ?: 34.0209
                val lng = loc?.second ?: -6.8416
                val tz = if (loc != null) "Africa/Casablanca" else "Africa/Casablanca"
                calculatePrayers(lat, lng, tz, methodName)
            }
        }
    }

    private fun calculatePrayers(lat: Double, lng: Double, timezone: String, methodName: String) {
        currentTimezone = timezone
        val zoneId = try { ZoneId.of(timezone) } catch (e: Exception) { ZoneId.systemDefault() }
        val today = LocalDate.now(zoneId)
        val tomorrow = today.plusDays(1)
        val method = CalculationMethodEnum.valueOf(methodName)
        val timesToday = PrayerCalculator.calculate(today, lat, lng, method, timezone)
        val timesTomorrow = PrayerCalculator.calculate(tomorrow, lat, lng, method, timezone)
        _prayerTimes.value = timesToday
        _nextPrayer.value = PrayerCalculator.nextPrayer(timesToday, timesTomorrow)
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (true) {
                val next = _nextPrayer.value
                if (next != null) {
                    val diff = next.second - System.currentTimeMillis()
                    if (diff <= 0) { refresh(); delay(1000); continue }
                    val h = (diff / 3600000).toInt()
                    val m = ((diff / 60000) % 60).toInt()
                    val s = ((diff / 1000) % 60).toInt()
                    _countdown.value = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
                }
                delay(1000)
            }
        }
    }

    fun searchCities(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) cityRepository.getAllMoroccoCities().collectLatest { _citySearchResults.value = it }
            else cityRepository.searchCities(query).collectLatest { _citySearchResults.value = it }
        }
    }

    fun selectCity(city: City) {
        viewModelScope.launch {
            preferences.updateCity(city.id, city.nameAr, city.latitude, city.longitude, city.timezone)
        }
    }

    fun enableGps() { viewModelScope.launch { preferences.setUseGps(true) } }

    fun setCalculationMethod(method: CalculationMethodEnum) {
        viewModelScope.launch { preferences.setCalculationMethod(method.name) }
    }

    fun refresh() {
        viewModelScope.launch {
            val prefs = preferences.preferencesFlow.first()
            if (prefs.useGps) fetchLocationAndCalculate(prefs.calculationMethod)
            else calculatePrayers(prefs.selectedCityLat, prefs.selectedCityLng, prefs.selectedCityTimezone, prefs.calculationMethod)
        }
    }
}
