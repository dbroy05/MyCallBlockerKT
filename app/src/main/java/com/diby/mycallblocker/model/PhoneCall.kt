package com.diby.mycallblocker.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Holds phone call data to be persisted on local db
 */
@Entity(primaryKeys = ["phone_number"])
class PhoneCall(@field:ColumnInfo(name = "phone_number")
                var phoneNumber: String, @field:ColumnInfo(name = "call_type")
                var callType: Int) {
    companion object {
        val CALL_TYPE_NORMAL = 1
        val CALL_TYPE_SUSPICIOUS = 2
        val CALL_TYPE_BLOCKED = 3
    }

}
