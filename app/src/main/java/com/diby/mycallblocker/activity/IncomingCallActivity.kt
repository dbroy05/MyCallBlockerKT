package com.diby.mycallblocker.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

import com.diby.mycallblocker.R

/**
 * The activity to show the status of call types. For normal calls with number found in the contact
 * the background is in Blue, but for suspicious calls, it shows the Suspicious Call in Red.
 * It also shows the status on locked phone screen.
 */

class IncomingCallActivity : Activity() {

    /**
     * Creates the screen with no title and width 80% for device screen width and 20% of screen
     * height.
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Shows on device locked screen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //No title showing
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.incoming_call_layout)

        //Re-sizes the screen 80% of screen width and 20% height.
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        window.setLayout((dm.widthPixels * 0.8).toInt(), (dm.heightPixels * 0.2).toInt())

        //Checks in intent if call is suspicious
        val isSuspiciousCall = intent.getBooleanExtra(CALL_SUSPICIOUS, false)
        val callerName = intent.getStringExtra(CALLER_NAME)
        //Sets call name
        val callerNameTxt = findViewById<TextView>(R.id.caller_name)
        //sets call type icon
        val callIcon = findViewById<ImageView>(R.id.call_icon)
        callerNameTxt.text = callerName
        if (isSuspiciousCall) { //for suspicious calls, sets the background and icon differently
            findViewById<View>(R.id.notify_dialog).setBackgroundColor(
                    resources.getColor(android.R.color.holo_red_dark))
            callerNameTxt.text = "Suspicious Call!"
            callIcon.setImageResource(R.drawable.span_call)
        }

        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val phoneNumber = findViewById<TextView>(R.id.phone_number)
        phoneNumber.text = number

    }

    companion object {

        val CALL_SUSPICIOUS = "Suspicious"
        val CALLER_NAME = "Name"
    }

}
