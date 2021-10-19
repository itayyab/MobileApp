package com.tayyab.mobileapp.interfaces

import com.android.volley.VolleyError
import com.tayyab.mobileapp.models.Category
import java.util.ArrayList

interface VolleyCallback {
    fun onSuccess(result: String)
    fun onError(error: VolleyError)
    fun onSuccessArrayList(result: ArrayList<Category>)
}