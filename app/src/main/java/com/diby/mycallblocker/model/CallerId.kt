package com.diby.mycallblocker.model

import com.google.gson.annotations.SerializedName

/**
 * Model to hold data for caller Id info from API call
 */

class CallerId {
    @SerializedName("data")
    var data: Data? = null

    class Data {
        var name: String? = null
    }

}
