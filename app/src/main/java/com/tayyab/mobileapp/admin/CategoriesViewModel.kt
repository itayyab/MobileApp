package com.tayyab.mobileapp.admin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.utils.ApiUtils
import com.tayyab.mobileapp.utils.Config

class CategoriesViewModel(application: Application) : AndroidViewModel(application) {
    private var volleyRequestQueue: RequestQueue? = Volley.newRequestQueue(application.applicationContext)


    var data: MutableLiveData<List<Category>>? = MutableLiveData()


    fun getProductsStart() = data!!
    fun getProducts() {
        Log.e("DBG:", "getProducts called")
        // volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
        val jsonreq = ApiUtils.GsonRequestForCat<ArrayList<Category>>(
            Config.BASE_URL + "Categories",
            null,
            { ServerResponse ->
                data!!.value = ServerResponse
//                    for (item: Product in ServerResponse) {
//                    println(item.pr_name)
//                    Log.e("RESP:", item.pr_name)
//                }
            }
        ) { volleyError ->

            Log.e("RESP:", volleyError.toString())
        }
        volleyRequestQueue?.add(jsonreq)


    }


}