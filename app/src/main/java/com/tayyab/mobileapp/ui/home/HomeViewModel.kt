package com.tayyab.mobileapp.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.ApiUtils
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    var volleyRequestQueue: RequestQueue? = Volley.newRequestQueue(application.applicationContext)


    var data: MutableLiveData<List<Product>>? = MutableLiveData()


    fun getProductsStart() = data!!
    fun getProducts() {

       // volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
        var jsonreq= ApiUtils.GsonRequest<ArrayList<Product>>(
            Config.BASE_URL + "Products/WithCategory",
            ArrayList(),
            null,
            { ServerResponse ->
                data!!.value =ServerResponse
//                    for (item: Product in ServerResponse) {
//                    println(item.pr_name)
//                    Log.e("RESP:", item.pr_name)
//                }
            },
            { volleyError ->

                Log.e("RESP:", volleyError.toString())
            });
        volleyRequestQueue?.add(jsonreq)


    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    private val _text2 = MutableLiveData<String>().apply  {
      //  value = "This is home Fragment"
        var test: String=" Testing"
        volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
        var jsonreq= ApiUtils.GsonRequest<ArrayList<Product>>(
            Config.BASE_URL + "Products/WithCategory",
            ArrayList(),
            null,
            { ServerResponse ->
                for (item: Product in ServerResponse) {
                    println(item.pr_name)
                    Log.e("RESP:", item.pr_name)
                }
            },
            { volleyError ->

                Log.e("RESP:", volleyError.toString())
            });
        volleyRequestQueue?.add(jsonreq)
        value = test
    }
    val text: LiveData<String> = _text
    val text2: LiveData<String> = _text2



}