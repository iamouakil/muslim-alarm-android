package com.iamouakil.muslimalarm.data.alarm
import androidx.room.TypeConverter
class Converters {
    @TypeConverter fun fromList(list: List<Int>?): String = list?.joinToString(",") ?: ""
    @TypeConverter fun toList(data: String?): List<Int> { if (data.isNullOrBlank()) return emptyList(); return data.split(",").mapNotNull { it.toIntOrNull() } }
}
