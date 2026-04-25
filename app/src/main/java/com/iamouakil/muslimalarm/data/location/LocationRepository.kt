package com.iamouakil.muslimalarm.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Flow<Pair<Double, Double>?> = flow {
        try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).await()
            emit(if (location != null) Pair(location.latitude, location.longitude) else null)
        } catch (e: Exception) {
            emit(null)
        }
    }
}
