package com.iamouakil.muslimalarm.di

import android.content.Context
import androidx.room.Room
import com.iamouakil.muslimalarm.data.city.CityDao
import com.iamouakil.muslimalarm.data.city.CityDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CityModule {

    @Provides
    @Singleton
    fun provideCityDatabase(@ApplicationContext context: Context): CityDatabase =
        Room.databaseBuilder(context, CityDatabase::class.java, "cities_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCityDao(database: CityDatabase): CityDao = database.cityDao()
}
