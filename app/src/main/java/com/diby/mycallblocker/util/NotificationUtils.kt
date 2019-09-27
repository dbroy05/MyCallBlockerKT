package com.diby.mycallblocker.util

/**
 * Created by rdibyendu on 2/27/18.
 */

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat

class NotificationUtils(base: Context) : ContextWrapper(base) {

    val manager: NotificationManager
        get() {
            if (mManager == null) {
                mManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager as NotificationManager
        }

    init {
        createChannels()
    }

    fun createChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create android channel
            val androidChannel = NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true)
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true)
            // Sets the notification light color for notifications posted to this channel
            androidChannel.lightColor = Color.GREEN
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            manager.createNotificationChannel(androidChannel)

        }
    }

    fun getChannelNotification(title: String, body: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
    }

    companion object {

        private var mManager: NotificationManager? = null
        val CHANNEL_ID = "com.diby.mycallblocker.notify"
        val CHANNEL_NAME = "My Callblocker CHANNEL"
    }
}
