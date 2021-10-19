package com.tayyab.mobileapp.ui.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.tayyab.mobileapp.models.Carts
import com.tayyab.mobileapp.utils.ApiUtils
import com.tayyab.mobileapp.utils.Config

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private var volleyRequestQueue: RequestQueue? =
        Volley.newRequestQueue(application.applicationContext)

    var data: MutableLiveData<List<Carts>>? = MutableLiveData()


    fun getCartsStart() = data!!
    fun getCarts(userid: String) {
        val jsonreq = ApiUtils.GsonRequestCarts<ArrayList<Carts>>(
            Config.BASE_URL + "Carts/" + userid,
            null,
            { ServerResponse ->
                data!!.value = ServerResponse
//                    for (item: CartDetail in ServerResponse.firstOrNull()?.cartDetails!!) {
////                    println(item.cart_id)
//                    Log.e("RESP:", item.product!!.pr_name)
//                        Log.e("RESP:", item.cD_Pr_Qty.toString())
//                        Log.e("RESP:", item.cD_Pr_Amnt.toString())
////                        Log.e("RESP:", item.category.toString())
//                }
            }
        ) { volleyError ->

            Log.e("RESP:", volleyError.toString())
        }
        volleyRequestQueue?.add(jsonreq)


    }
}