package com.tayyab.mobileapp.interfaces

import com.tayyab.mobileapp.models.Category
import java.util.ArrayList

interface VolleyCallback {
    fun onSuccess(result: String)
    fun onSuccessArrayList(result: ArrayList<Category>)
}