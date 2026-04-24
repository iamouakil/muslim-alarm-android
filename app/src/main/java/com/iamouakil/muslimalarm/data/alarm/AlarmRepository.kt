package com.iamouakil.muslimalarm.data.alarm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao) {
    fun getAllAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarms()
    suspend fun getEnabledAlarms(): List<Alarm> = alarmDao.getEnabledAlarms()
    suspend fun getAlarmById(id: Int): Alarm? = alarmDao.getAlarmById(id)
    suspend fun insert(alarm: Alarm): Long = alarmDao.insert(alarm)
    suspend fun update(alarm: Alarm) = alarmDao.update(alarm)
    suspend fun delete(alarm: Alarm) = alarmDao.delete(alarm)
}
