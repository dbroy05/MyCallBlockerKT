package com.diby.mycallblocker.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import com.diby.mycallblocker.model.PhoneCall

import android.arch.persistence.room.OnConflictStrategy.REPLACE

/**
 * Data Access Object to handle/access data on local Room Database
 */

@Dao
interface PhoneCallDao {
    //Saves a call
    @Insert(onConflict = REPLACE)
    fun save(phoneCall: PhoneCall)

    //Gets live data based on call type
    @Query("SELECT * FROM PhoneCall WHERE call_type = :callType")
    fun loadByCallType(callType: Int): LiveData<Array<PhoneCall>>

    //Checks if the number is found in local db
    @Query("SELECT * FROM PhoneCall WHERE phone_number = :phoneNumber")
    fun loadByNumber(phoneNumber: String): PhoneCall

    //Deletes the phonecall from local db
    @Delete
    fun delete(vararg phoneCall: PhoneCall)
}
