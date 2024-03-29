package com.diby.mycallblocker.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * ViewModelFactory to create the view model
 */
@Singleton
class CallViewModelFactory @Inject
constructor(private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        if (creator == null) {
            throw IllegalArgumentException("unknown model class $modelClass")
        }
        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
