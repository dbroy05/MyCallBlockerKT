package com.diby.mycallblocker.di

import com.diby.mycallblocker.receiver.PhoneCallBroadcastReceiver

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Required by Dagger DI for BroadcastReceiver building
 */

@Module
abstract class BroadcastReceiverModule {
    @ContributesAndroidInjector
    internal abstract fun contributesPhoneCallBroadcastReceiver(): PhoneCallBroadcastReceiver
}
