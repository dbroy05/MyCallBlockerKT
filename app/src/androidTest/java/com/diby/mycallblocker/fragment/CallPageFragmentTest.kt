/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diby.mycallblocker.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.diby.mycallblocker.EspressoTestUtil
import com.diby.mycallblocker.R
import com.diby.mycallblocker.RecyclerViewMatcher
import com.diby.mycallblocker.SingleFragmentActivity
import com.diby.mycallblocker.TestUtil
import com.diby.mycallblocker.ViewModelUtil
import com.diby.mycallblocker.model.PhoneCall
import com.diby.mycallblocker.viewmodel.CallViewModel

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.view.View
import org.hamcrest.CoreMatchers.not
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Testing the fragment component
 */

@RunWith(AndroidJUnit4::class)
class CallPageFragmentTest {
    @Rule
    var activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)

    private var viewModel: CallViewModel? = null
    private val userData = MutableLiveData<Array<PhoneCall>>()

    @Before
    @Throws(Throwable::class)
    fun init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
        val fragment = CallPageFragment.create(0)
        viewModel = mock(CallViewModel::class.java)
        `when`<LiveData<Array<PhoneCall>>>(viewModel!!.phoneCalls).thenReturn(userData)

        fragment.setViewModelFactory(ViewModelUtil.createFor<CallViewModel>(viewModel))
        activityRule.activity.setFragment(fragment)
    }

    @Test
    fun loadingWithPhoneCall() {
        val phoneCall = TestUtil.createPhoneCall("4259501212", PhoneCall.CALL_TYPE_SUSPICIOUS)
        userData.postValue(arrayOf(phoneCall))
        onView(withId(R.id.phone_number)).check(matches(withText(phoneCall.phoneNumber)))

    }

    @Test
    fun nullUser() {
        userData.postValue(null)
        onView(withId(R.id.phone_number)).check(matches(not<View>(isDisplayed())))
    }


    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.recyclerView)
    }

}