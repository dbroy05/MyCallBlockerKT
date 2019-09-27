package com.diby.mycallblocker.adapter

/**
 * Created by rdibyendu on 2/28/18.
 */

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.diby.mycallblocker.R
import com.diby.mycallblocker.fragment.CallPageFragment
import com.diby.mycallblocker.model.PhoneCall

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
class CallListAdapter
/**
 * Initialize the dataset of the Adapter.
 *
 * @param dataSet PhoneCall[] containing the data to populate views to be used by RecyclerView.
 * @param fragment
 */
(private val mDataSet: Array<PhoneCall>, private val mFragment: CallPageFragment) : RecyclerView.Adapter<CallListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView
        val callerTypeImg: ImageView
        var cta: ImageView

        init {
            textView = v.findViewById(R.id.phone_number)
            callerTypeImg = v.findViewById(R.id.caller_type_icon)
            cta = v.findViewById(R.id.cta)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        val phoneCall = mDataSet[position]
        val formattedPhoneNumber = PhoneNumberUtils.formatNumber(phoneCall.phoneNumber, "US")
        viewHolder.textView.text = formattedPhoneNumber
        viewHolder.callerTypeImg.setImageResource(R.drawable.normal_profile)
        viewHolder.cta.setImageResource(R.drawable.info)
        if (phoneCall.callType == PhoneCall.CALL_TYPE_SUSPICIOUS) {
            viewHolder.callerTypeImg.setImageResource(R.drawable.span_call)
        } else if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
            viewHolder.callerTypeImg.setImageResource(R.drawable.blocked_call)
            viewHolder.cta.setImageResource(R.drawable.unblock)
        }

        viewHolder.cta.setOnClickListener {
            var message = mFragment.getString(R.string.dialog_block_message, phoneCall.phoneNumber)
            var title = mFragment.getString(R.string.dialog_title)
            var buttonLabel = mFragment.getString(R.string.block_label)
            if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                title = mFragment.getString(R.string.dialog_unblocktitle)
                message = mFragment.getString(R.string.dialog_unblock_message, phoneCall.phoneNumber)
                buttonLabel = mFragment.getString(R.string.unblock_label)
            }
            //Sets up the dialog when the info icon is clicked from the list item.
            val builder = AlertDialog.Builder(mFragment.activity)
            builder.setMessage(message)
                    .setTitle(title).setIcon(R.drawable.block_tab)
                    .setPositiveButton(buttonLabel) { dialogInterface, i ->
                        if (phoneCall.callType == PhoneCall.CALL_TYPE_BLOCKED) {
                            phoneCall.callType = PhoneCall.CALL_TYPE_NORMAL
                            mFragment.unBlockCall(phoneCall)
                        } else {
                            phoneCall.callType = PhoneCall.CALL_TYPE_BLOCKED
                            mFragment.blockCall(phoneCall)
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { dialogInterface, i -> dialogInterface.dismiss() }.show()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return mDataSet.size
    }


    /**
     * Interface to handle Block and Unblock call types.
     */
    interface BlockCallHandler {
        fun blockCall(phoneCall: PhoneCall)

        fun unBlockCall(phoneCall: PhoneCall)
    }

    companion object {
        private val TAG = "CallListAdapter"
    }
}

