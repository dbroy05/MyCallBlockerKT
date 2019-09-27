package com.diby.mycallblocker.service

import android.content.Context

import com.diby.mycallblocker.model.PhoneCall

import javax.inject.Singleton

/**
 * Main service to figure the call type
 */

@Singleton
class CallInfoService {

    /*
    This method should call the actual service endpoint to verify call type : Suspicious or Blocked.
    Currently, it's just hardcoded.
     */

    fun getCallInfo(phoneNumber: String): PhoneCall {
        var phoneCall = PhoneCall(phoneNumber, PhoneCall.CALL_TYPE_NORMAL)
        //
        if (phoneNumber.contains(SPAM_CALL)) {
            phoneCall = PhoneCall(phoneNumber, PhoneCall.CALL_TYPE_SUSPICIOUS)
        }

        if (phoneNumber.contains(BLOCK_CALL)) {
            phoneCall = PhoneCall(phoneNumber, PhoneCall.CALL_TYPE_BLOCKED)
        }

        return phoneCall
    }

    companion object {
        internal val SPAM_CALL = "4259501212"
        internal val BLOCK_CALL = "2539501212"
    }


}
