package com.diby.mycallblocker.receiver

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.persistence.room.util.StringUtil
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Handler
import android.provider.ContactsContract
import android.support.v4.app.NotificationCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log

import com.android.internal.telephony.ITelephony
import com.diby.mycallblocker.activity.IncomingCallActivity
import com.diby.mycallblocker.model.CallerId
import com.diby.mycallblocker.model.PhoneCall
import com.diby.mycallblocker.repository.CallRepository
import com.diby.mycallblocker.util.NotificationUtils

import java.lang.reflect.Method

import javax.inject.Inject

import dagger.android.AndroidInjection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * The main incoming call processor to apply various rules to determine call types and notifies the
 * user for the same.
 */

class PhoneCallBroadcastReceiver : BroadcastReceiver() {

    @Inject
    var callRepo: CallRepository? = null

    internal lateinit var telephonyManager: TelephonyManager
    internal lateinit var mNotificationUtils: NotificationUtils
    internal lateinit var mBuilder: NotificationCompat.Builder
    private val mNotificationId = 111111
    private var mContext: Context? = null


    override fun onReceive(context: Context, intent: Intent) {

        AndroidInjection.inject(this, context)

        mContext = context

        // TELEPHONY MANAGER class object to register one listner
        telephonyManager = context
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        //Create Listener
        val PhoneListener = MyPhoneStateListener()

        // Register listener for LISTEN_CALL_STATE
        telephonyManager.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE)

        mNotificationUtils = NotificationUtils(context)

    }

    /**
     * Listener for handling the call state
     */
    private inner class MyPhoneStateListener : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {

                //Check if the number exists in Contacts
                val contactName = getContactNameInContacts(incomingNumber)
                if (contactName != null) {  //If number found in contact, just notify normal call.
                    notifyCall(false, incomingNumber, contactName)
                } else {    //If not in contact, try to get Call Info from the repository
                    val onCompInter = object : CallRepository.OnCompleteListener {
                        override fun onComplete(phoneCall: PhoneCall) {
                            when (phoneCall.callType) {
                                PhoneCall.CALL_TYPE_SUSPICIOUS ->
                                    //For suspicious call, notify suspicious call
                                    notifyCall(true, incomingNumber, null)
                                PhoneCall.CALL_TYPE_BLOCKED ->
                                    //For blocked call, just block the call
                                    blockCall(incomingNumber)

                                else ->
                                    //Block all calls that are not in contact by default
                                    blockCall(incomingNumber)
                            }
                        }

                    }
                    callRepo!!.getCallInfo(incomingNumber, onCompInter)
                }


            }
        }

            /**
             * Check of the number is in Contact list
             * @param incomingPhoneNumber
             * @return name of the contact
             */
            fun getContactNameInContacts(incomingPhoneNumber: String): String? {
                val phones = mContext!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
                while (phones!!.moveToNext()) {
                    val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var phNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    phNumber = phNumber.replace("(", "").replace(")", "")
                            .replace("-", "").replace(" ", "")

                    if (incomingPhoneNumber.contains(phNumber)) {
                        phones.close()
                        return name
                    }

                }
                phones.close()
                return null
            }

            /**
             * Notifies user about the call
             * @param isSuspicious
             * @param incomingNumber
             * @param contactName
             */
            fun notifyCall(isSuspicious: Boolean, incomingNumber: String, contactName: String?) {
                //Main intent to show the call status on an Activity
                val intent = Intent(mContext, IncomingCallActivity::class.java)
                intent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER, incomingNumber)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

                if (!isSuspicious) { //For non suspicious calls
                    //If number exists in Contacts, pass the contact name, no need to call CallerId service
                    if (contactName != null && contactName.matches("\\d+(?:\\.\\d+)?".toRegex())) {
                        Handler().postDelayed({
                            intent.putExtra(IncomingCallActivity.CALLER_NAME, contactName)
                            mContext!!.startActivity(intent)
                        }, 1000)
                    } else {
                        //Get caller id only when the name is not in contact
                        callRepo!!.getCallerId(incomingNumber).enqueue(object : Callback<CallerId> {
                            override fun onResponse(call: Call<CallerId>, response: Response<CallerId>) {
                                Handler().postDelayed({
                                    intent.putExtra(IncomingCallActivity.CALLER_NAME, response.body()!!.data!!.name)
                                    mContext!!.startActivity(intent)
                                }, 500)
                            }

                            override fun onFailure(call: Call<CallerId>, t: Throwable) {}
                        })
                    }

                } else { //For suspicious call set the suspicious flag for the showing activity
                    Handler().postDelayed({
                        intent.putExtra(IncomingCallActivity.CALL_SUSPICIOUS, true)
                        mContext!!.startActivity(intent)
                    }, 1000)

                    //Notify user about suspicious info, so they can check later
                    mBuilder = mNotificationUtils.getChannelNotification("Suspicious Call!", "You got a suspicious call from number $incomingNumber")
                    mNotificationUtils.manager.notify(mNotificationId, mBuilder.build())
                }

            }

            /**
             * Blockes the incoming call
             * @param incomingNumber
             */
            fun blockCall(incomingNumber: String?) {
                try {
                    //Gets the TelephonyManager's getITelephony by reflection
                    val m = telephonyManager.javaClass.getDeclaredMethod("getITelephony")

                    m.isAccessible = true
                    val telephonyService = m.invoke(telephonyManager) as ITelephony

                    if (incomingNumber != null) {
                        telephonyService.endCall() //Ends the call, so it goes straight to VoiceMail
                    }
                    //Record the call on local db
                    callRepo!!.saveCall(PhoneCall(incomingNumber!!, PhoneCall.CALL_TYPE_BLOCKED))

                    //Notify user about the blocked call info
                    mBuilder = mNotificationUtils.getChannelNotification("Blocked Call", "The call from number $incomingNumber has been blocked.")
                    mNotificationUtils.manager.notify(mNotificationId, mBuilder.build())

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
    }


}
