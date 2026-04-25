package com.iamouakil.muslimalarm.data.city

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepository @Inject constructor(
    private val cityDao: CityDao,
    @ApplicationContext private val context: Context
) {
    suspend fun loadCitiesFromAssetsIfEmpty() {
        withContext(Dispatchers.IO) {
            if (cityDao.getCount() == 0) {
                val json = context.assets.open("cities.json").bufferedReader().use { it.readText() }
                val arr = JSONArray(json)
                val cities = (0 until arr.length()).map {
                    val o = arr.getJSONObject(it)
                    City(
                        id = o.getInt("id"),
                        nameAr = o.getString("nameAr"),
                        nameEn = o.getString("nameEn"),
                        country = o.getString("country"),
                        countryAr = o.getString("countryAr"),
                        latitude = o.getDouble("latitude"),
                        longitude = o.getDouble("longitude"),
                        timezone = o.getString("timezone")
                    )
                }
                cityDao.insertAll(cities)
            }
        }
    }

    fun searchCities(query: String): Flow<List<City>> = cityDao.searchCities(query)
    fun getAllMoroccoCities(): Flow<List<City>> = cityDao.getAllMoroccoCities()
}
