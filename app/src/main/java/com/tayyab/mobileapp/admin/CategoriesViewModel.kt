package com.tayyab.mobileapp.admin

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.ApiUtils

class CategoriesViewModel (application: Application) : AndroidViewModel(application) {
    var volleyRequestQueue: RequestQueue? = Volley.newRequestQueue(application.applicationContext)


    var data: MutableLiveData<List<Category>>? = MutableLiveData()


    fun getProductsStart() = data!!
    fun getProducts() {
        Log.e("DBG:", "getProducts called")
        // volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
        var jsonreq= ApiUtils.GsonRequestForCat<ArrayList<Category>>(
            Config.BASE_URL + "Categories",
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

//    private val _text2 = MutableLiveData<String>().apply  {
//        //  value = "This is home Fragment"
//        var test: String=" Testing"
//        volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
//        var jsonreq= ApiUtils.GsonRequest<ArrayList<Category>>(
//            Config.BASE_URL + "Products/WithCategory",
//            ArrayList(),
//            null,
//            { ServerResponse ->
//                for (item: Category in ServerResponse) {
////                    println(item.pr_name)
////                    Log.e("RESP:", item.pr_name)
//                }
//            },
//            { volleyError ->
//
//                Log.e("RESP:", volleyError.toString())
//            });
//        volleyRequestQueue?.add(jsonreq)
//        value = test
//    }
    val text: LiveData<String> = _text
//    val text2: LiveData<String> = _text2



}