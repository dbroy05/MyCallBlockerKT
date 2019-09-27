package com.diby.mycallblocker.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.diby.mycallblocker.annotation.ViewModelKey
import com.diby.mycallblocker.factory.CallViewModelFactory
import com.diby.mycallblocker.viewmodel.CallViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Required by Dagger DI for binding the ViewModel required by Fragment
 */

@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CallViewModel::class)
    internal abstract fun bindCallViewModel(callViewModel: CallViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: CallViewModelFactory): ViewModelProvider.Factory
}
