package com.iamouakil.muslimalarm.data.city

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("""
        SELECT * FROM cities
        WHERE nameAr LIKE '%' || :query || '%'
        OR nameEn LIKE '%' || :query || '%'
        OR country LIKE '%' || :query || '%'
        OR countryAr LIKE '%' || :query || '%'
        ORDER BY CASE WHEN country = 'MA' THEN 0 ELSE 1 END
        LIMIT 50
    """)
    fun searchCities(query: String): Flow<List<City>>

    @Query("SELECT * FROM cities WHERE country = 'MA' ORDER BY nameAr")
    fun getAllMoroccoCities(): Flow<List<City>>

    @Query("SELECT * FROM cities WHERE id = :id LIMIT 1")
    suspend fun getCityById(id: Int): City?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cities: List<City>)

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun getCount(): Int
}
