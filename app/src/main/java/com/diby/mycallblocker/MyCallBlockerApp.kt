package com.diby.mycallblocker

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver

import com.diby.mycallblocker.di.AppInjector

import javax.inject.Inject

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector

/**
 * Main application to inject all DI modules and components namely Activity and BroadcastReceiver
 */

class MyCallBlockerApp : Application(), HasActivityInjector, HasBroadcastReceiverInjector {
    @Inject
    internal var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null

    @Inject
    internal var broadcastReceiverInjector: DispatchingAndroidInjector<BroadcastReceiver>? = null

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver>? {
        return broadcastReceiverInjector
    }
}
