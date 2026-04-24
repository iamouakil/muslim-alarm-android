package com.iamouakil.muslimalarm.data.alarm
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms") fun getAllAlarms(): Flow<List<Alarm>>
    @Query("SELECT * FROM alarms WHERE isEnabled = 1") suspend fun getEnabledAlarms(): List<Alarm>
    @Query("SELECT * FROM alarms WHERE id = :id") suspend fun getAlarmById(id: Int): Alarm?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(alarm: Alarm): Long
    @Update suspend fun update(alarm: Alarm)
    @Delete suspend fun delete(alarm: Alarm)
}
