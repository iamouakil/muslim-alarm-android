package com.iamouakil.muslimalarm.data.city

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    @PrimaryKey val id: Int,
    val nameAr: String,
    val nameEn: String,
    val country: String,
    val countryAr: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
)
