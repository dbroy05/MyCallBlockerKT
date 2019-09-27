package com.diby.mycallblocker.repository

import android.arch.lifecycle.LiveData
import android.database.Cursor
import android.os.AsyncTask
import android.provider.ContactsContract

import com.diby.mycallblocker.api.CallerIdLookupService
import com.diby.mycallblocker.dao.PhoneCallDao
import com.diby.mycallblocker.model.CallerId
import com.diby.mycallblocker.model.PhoneCall
import com.diby.mycallblocker.service.CallInfoService

import javax.inject.Inject
import javax.inject.Singleton

import retrofit2.Call

/**
 * Main repository to manage data from different sources : API Services or local DataSource
 */
@Singleton
class CallRepository
/**
 * Injected by Dagger DI
 * @param callerIdLookupService
 * @param callInfoService
 * @param phoneCallDao
 */
@Inject
constructor(
        /**
         * CallerId look up service to show the caller id on phone call
         */
        private val callerIdLookupService: CallerIdLookupService,
        /**
         * Uses API service to determine call types
         */
        private val callInfoService: CallInfoService,
        /**
         * Uses DAO to handle data in local database
         */
        private val phoneCallDao: PhoneCallDao) {

    /**
     * Returns the PhoneCall LiveData from DAO based on call type
     * @param callType
     * @return
     */
    fun getCalls(callType: Int): LiveData<Array<PhoneCall>> {
        return phoneCallDao.loadByCallType(callType)
    }

    /**
     * Saves the phone call to local DB thru DAO
     * @param phoneCall
     */
    fun saveCall(phoneCall: PhoneCall) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                phoneCallDao.save(phoneCall)
                return null
            }
        }.execute()
    }

    /**
     * Delete a call from local db
     * @param phoneCall
     */
    fun deleteCall(phoneCall: PhoneCall) {
        object : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                phoneCallDao.delete(phoneCall)
                return null
            }
        }.execute()
    }

    /**
     * Checks if the number call type is found locally, if not fetches from the callInfoService API
     * and saves locally.
     * @param phoneNumber
     * @param listener
     */
    fun getCallInfo(phoneNumber: String, listener: OnCompleteListener) {

        object : AsyncTask<Void, Void, PhoneCall>() {

            override fun doInBackground(vararg voids: Void): PhoneCall {
                var phoneCall: PhoneCall? = phoneCallDao.loadByNumber(phoneNumber)
                if (phoneCall == null) {
                    phoneCall = callInfoService.getCallInfo(phoneNumber)
                    if (phoneCall!!.callType == PhoneCall.CALL_TYPE_SUSPICIOUS || phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                        phoneCallDao.save(phoneCall)
                    }

                }
                return phoneCall
            }

            override fun onPostExecute(phoneCall: PhoneCall) {
                listener.onComplete(phoneCall)
            }
        }.execute()


    }

    /**
     * Interface to be passed for handling result from getCallInfo method call
     */
    interface OnCompleteListener {
        fun onComplete(phoneCall: PhoneCall)
    }

    /**
     * Returns the handle for the result of Caller id info of a number
     * @param phoneNumber
     * @return
     */
    fun getCallerId(phoneNumber: String): Call<CallerId> {
        return callerIdLookupService.getCallerId(phoneNumber)
    }

}
