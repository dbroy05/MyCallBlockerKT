package com.diby.mycallblocker.di

import android.app.Application

import com.diby.mycallblocker.MyCallBlockerApp

import javax.inject.Singleton

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

/**
 * AppComponent for Dagger DI to hold all modules
 */

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, MainActivityModule::class, BroadcastReceiverModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(myCallBlockerApp: MyCallBlockerApp)
}
