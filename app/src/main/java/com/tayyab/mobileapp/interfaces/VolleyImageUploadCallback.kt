package com.tayyab.mobileapp.interfaces

import com.android.volley.VolleyError

interface VolleyImageUploadCallback {
    fun onSuccess(result: String)
    fun onError(error: VolleyError)
    fun onAuthFailed(statusCode: Int)
}