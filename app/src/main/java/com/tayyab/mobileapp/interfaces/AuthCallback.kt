package com.tayyab.mobileapp.interfaces

import com.android.volley.VolleyError
import com.tayyab.mobileapp.models.Category
import org.json.JSONObject
import java.util.ArrayList

interface AuthCallback {
    fun onSuccess(result: JSONObject)
    fun onFailed(error:VolleyError)
}