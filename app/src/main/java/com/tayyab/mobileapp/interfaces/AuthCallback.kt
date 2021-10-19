package com.tayyab.mobileapp.interfaces

import com.android.volley.VolleyError
import org.json.JSONObject

interface AuthCallback {
    fun onSuccess(result: JSONObject)
    fun onFailed(error: VolleyError)
}