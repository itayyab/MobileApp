package com.tayyab.mobileapp.interfaces

import com.android.volley.VolleyError
import org.json.JSONObject

interface VolleyCRUDCallback {
    fun onSuccess(result: JSONObject)
    fun onError(error:VolleyError)
    fun onAuthFailed(statusCode:Int)
}