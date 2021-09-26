package com.tayyab.mobileapp.activities

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.R
import com.tayyab.mobileapp.models.Product
import com.tayyab.mobileapp.utils.ApiUtils
import com.tayyab.mobileapp.utils.VolleySingleton

class MainActivityAdminViewModel (application: Application) : AndroidViewModel(application) {
    var data: MutableLiveData<Int>? = MutableLiveData()
    val queue = VolleySingleton.getInstance(application.applicationContext)
    var dailog: MutableLiveData<Boolean>? = MutableLiveData()

    fun getDialogStart() = dailog!!

    fun getDialogs(product: Boolean) {
        dailog!!.value =product
    }


    fun getProductsStart() = data!!
    fun getProducts(user:String) {

        Log.e("TESTTTTTT","TESTSESTTS")
        // volleyRequestQueue = Volley.newRequestQueue(application.applicationContext)


        val jsonArrayRequest = object : StringRequest(
            Request.Method.GET,
            Config.BASE_URL +"Carts/GetCartCount/"+user,
            Response.Listener { response ->
                data!!.value =response.toInt()
//                var token=  response.get("token").toString()
//                var extract= token.split(".")[1]
//                val decodedBytes = Base64.decode(extract, Base64.DEFAULT);
//                val decodedString = String(decodedBytes)
//                val javaRootMapObject: Map<*, *> = Gson().fromJson(
//                    decodedString,
//                    Map::class.java
//                )
//                var badge = bottomNavigationView.getOrCreateBadge(R.id.nav_gallery)
//                badge.isVisible = true
//// An icon only badge will be displayed unless a number is set:
//                badge.number = response.toInt()
//                Toast.makeText(applicationContext,response.toString(), Toast.LENGTH_LONG).show()
//                //                appSettings?.saveLoggedIn(true)
//                appSettings?.saveToken(token)
//                appSettings?.saveIsAdmin(javaRootMapObject.get("UserID")!!.equals("ADMIN"))
//                val intent = Intent(this@LoginFragment.context, MainActivityShop::class.java)
//                //intent.putExtra("WID", obj.WID)
//                startActivity(intent)
            },
            Response.ErrorListener { error ->// Do something when error occurred
//                Toast.makeText(applicationContext,error.message.toString(), Toast.LENGTH_LONG).show()
                Log.e("REEEEE:",error.message.toString())
            }
        ){
//            override fun getHeaders(): Map<String, String>? {
//                val headers: HashMap<String, String> = HashMap()
//                //  headers["Authorization"] = "Basic $base64EncodedCredentials"
//                headers["Authorization"] = "Bearer "+appSettings?.getToken()
//                return headers
//            }
        }
       /* var jsonreq= ApiUtils.GsonRequest<ArrayList<Product>>(
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
            });*/
//        volleyRequestQueue?.add(jsonreq)
        queue.addToRequestQueue(jsonArrayRequest)
       // VolleySingleton.getInstance(application.applicationContext).addToRequestQueue(jsonArrayRequest)


    }
}