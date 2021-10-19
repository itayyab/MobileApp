package com.tayyab.mobileapp.ui.shop

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.tayyab.mobileapp.utils.Config
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.ApiUtils
import kotlin.collections.ArrayList

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private var volleyRequestQueue: RequestQueue? = Volley.newRequestQueue(application.applicationContext)


    var data: MutableLiveData<List<Product>>? = MutableLiveData()


    fun getProductsStart() = data!!
    fun getProducts() {

       // volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)
        val jsonreq= ApiUtils.GsonRequest<ArrayList<Product>>(
            Config.BASE_URL + "Products/WithCategory",
            null,
            { ServerResponse ->
                data!!.value =ServerResponse
//                    for (item: Product in ServerResponse) {
//                    println(item.pr_name)
//                    Log.e("RESP:", item.pr_name)
//                        Log.e("RESP:", item.category.toString())
//                }
            }
        ) { volleyError ->

            Log.e("RESP:", volleyError.toString())
        }
        volleyRequestQueue?.add(jsonreq)


    }

}