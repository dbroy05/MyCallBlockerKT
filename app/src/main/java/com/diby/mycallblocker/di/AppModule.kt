package com.diby.mycallblocker.di

import android.app.Application
import android.arch.persistence.room.Room

import com.diby.mycallblocker.api.CallerIdLookupService
import com.diby.mycallblocker.dao.PhoneCallDao
import com.diby.mycallblocker.database.CallDatabase
import com.diby.mycallblocker.service.CallInfoService
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Manages the Provider for all different components like DAO and Services
 */
@Module(includes = [ViewModelModule::class])
internal class AppModule {

    @Singleton
    @Provides
    fun provideCallerIdLookupService(): CallerIdLookupService {
        return Retrofit.Builder()
                .baseUrl("https://api.everyoneapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CallerIdLookupService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): CallDatabase {
        return Room.databaseBuilder(app, CallDatabase::class.java, "phonecall.db").build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: CallDatabase): PhoneCallDao {
        return db.phoneCallDao()
    }

    @Singleton
    @Provides
    fun provideCallInfoService(): CallInfoService {
        return CallInfoService()
    }
}
