package com.diby.mycallblocker.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import com.diby.mycallblocker.dao.PhoneCallDao
import com.diby.mycallblocker.model.PhoneCall

/**
 * Room database to save data locally
 */
@Database(entities = [PhoneCall::class], version = 1)
abstract class CallDatabase : RoomDatabase() {
    abstract fun phoneCallDao(): PhoneCallDao
}
