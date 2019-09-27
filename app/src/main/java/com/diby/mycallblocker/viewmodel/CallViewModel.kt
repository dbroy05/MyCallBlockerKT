package com.diby.mycallblocker.viewmodel

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.database.Cursor
import android.provider.CallLog
import android.support.annotation.VisibleForTesting
import android.telephony.PhoneNumberUtils

import com.diby.mycallblocker.model.PhoneCall
import com.diby.mycallblocker.repository.CallRepository

import java.util.ArrayList
import java.util.Collections

import javax.inject.Inject

/**
 * ViewModel that drives the CallPageFragment UI data. It abstracts the logic from fragment
 * to determine data for three different call types.
 */

class CallViewModel @Inject // CallRepository parameter is provided by Dagger 2
constructor(
        /**
         * Uses repository to load data for different call types
         */
        val callRepo: CallRepository) : ViewModel() {

    /**
     * Holds LiveData on list of PhoneCalls to be observed by fragment
     */
    @get:VisibleForTesting
    var phoneCalls: LiveData<Array<PhoneCall>>? = null
        private set

    /**
     * Loads data based on call types and makes that ready for fragment
     * @param ctx
     * @param position
     */
    fun init(ctx: Activity, position: Int) {

        if (position == 0) { //Recent Calls
            val mutableLiveData = getRecentCallLiveData(ctx)
            phoneCalls = mutableLiveData

        } else if (position == 1) { //Suspicious Calls
            phoneCalls = callRepo.getCalls(PhoneCall.CALL_TYPE_SUSPICIOUS)
        } else if (position == 2) { //Blocked Calls
            phoneCalls = callRepo.getCalls(PhoneCall.CALL_TYPE_BLOCKED)
        }
    }

    /**
     * Loads recent call from device phone call logs
     * @param ctx
     * @return
     */
    private fun getRecentCallLiveData(ctx: Activity): MutableLiveData<Array<PhoneCall>> {
        val normalCalls = ArrayList<PhoneCall>()
        val managedCursor = ctx.managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null)
        val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        while (managedCursor.moveToNext()) {
            val phNumber = managedCursor.getString(number)

            if (phNumber != null && !phNumber.isEmpty()) {
                if (!numberExist(normalCalls, phNumber)) {
                    normalCalls.add(PhoneCall(phNumber, PhoneCall.CALL_TYPE_NORMAL))
                }
            }

        }
        //Reverses to show the latest on top of the list
        Collections.reverse(normalCalls)
        //Creates the LiveData with recent calls
        val mutableLiveData = MutableLiveData<Array<PhoneCall>>()
        mutableLiveData.value = normalCalls.toTypedArray()
        return mutableLiveData
    }

    /**
     * Util method to check if number already exists in the list
     * @param normalCalls
     * @param phNumber
     * @return
     */
    private fun numberExist(normalCalls: ArrayList<PhoneCall>, phNumber: String): Boolean {
        for (phoneCall in normalCalls) {
            if (phoneCall.phoneNumber.contains(phNumber)) return true
        }
        return false
    }

}
