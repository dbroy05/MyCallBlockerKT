package com.diby.mycallblocker.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.diby.mycallblocker.R
import com.diby.mycallblocker.adapter.CallListAdapter
import com.diby.mycallblocker.di.Injectable
import com.diby.mycallblocker.model.PhoneCall
import com.diby.mycallblocker.viewmodel.CallViewModel

import javax.inject.Inject

/**
 * Shows list of calls for three different call types. Handles blocking or unblocking calls from
 * the list.
 */

class CallPageFragment : Fragment(), Injectable, CallListAdapter.BlockCallHandler {

    /**
     * The main list for showing the call list
     */
    protected lateinit var mRecyclerView: RecyclerView

    /**
     * Adapter for the call list RecyclerView
     */
    private var mAdapter: CallListAdapter? = null

    /**
     * Holds the current position
     */
    private var position: Int = 0

    /**
     * Injected by Dagger DI
     */
    @Inject
    internal var viewModelFactory: ViewModelProvider.Factory? = null

    /**
     * Uses the ViewModel to drive data on view
     */
    private var viewModel: CallViewModel? = null

    /**
     * Creates the Fragment with position
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            position = arguments.getInt(FRAGMENT_POS)
        }
    }

    /**
     * On fragment view create, it creates the recyclerview with its layout manager
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(
                R.layout.fragment_screen_slide, container, false) as ViewGroup

        mRecyclerView = rootView.findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        return rootView
    }

    /**
     * Once activity created, the ViewModel is introduced to observe LiveData for any data change
     * to update UI accordingly to better lifecycle management.
     * @param savedInstanceState
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Creates the viewmodel to initialize
        viewModel = ViewModelProviders.of(this, viewModelFactory!!).get(CallViewModel::class.java)
        viewModel!!.init(activity, position)
        //Observes for PhoneCall LiveData to add adapter to the recyclerview
        viewModel!!.phoneCalls?.observe(this, Observer { phoneCalls ->
            mAdapter = CallListAdapter(phoneCalls!!, this@CallPageFragment)
            mRecyclerView.adapter = mAdapter
        })

    }

    /**
     * Handles blocking the call from the list item.
     * @param phoneCall
     */
    override fun blockCall(phoneCall: PhoneCall) {
        viewModel!!.callRepo.saveCall(phoneCall)
    }

    /**
     * Handles unblocking the call from the list item.
     * @param phoneCall
     */
    override fun unBlockCall(phoneCall: PhoneCall) {
        viewModel!!.callRepo.deleteCall(phoneCall)
    }

    companion object {

        val FRAGMENT_POS = "position"

        /**
         * For testing
         * @param position
         * @return
         */
        fun create(position: Int): CallPageFragment {
            val userFragment = CallPageFragment()
            val bundle = Bundle()
            bundle.putInt(FRAGMENT_POS, position)
            userFragment.arguments = bundle
            return userFragment
        }
    }
}
