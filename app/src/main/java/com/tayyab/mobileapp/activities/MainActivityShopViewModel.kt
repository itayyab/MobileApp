package com.tayyab.mobileapp.activities

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tayyab.mobileapp.utils.Config
import com.tayyab.mobileapp.utils.VolleySingleton

class MainActivityShopViewModel(application: Application) : AndroidViewModel(application) {
    var data: MutableLiveData<Int>? = MutableLiveData()
    private val queue = VolleySingleton.getInstance(application.applicationContext)
    fun getProductsStart() = data!!
    fun getProducts(user: String) {

        val jsonArrayRequest = object : StringRequest(
            Method.GET,
            Config.BASE_URL + "Carts/GetCartCount/" + user,
            Response.Listener { response ->
                data!!.value = response.toInt()
            },
            Response.ErrorListener { error ->// Do something when error occurred
//                Toast.makeText(applicationContext,error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("REEEEE:", error.message.toString())
            }
        ) {
        }
        queue.addToRequestQueue(jsonArrayRequest)
    }
}