package com.tayyab.mobileapp.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.tayyab.mobileapp.Config
import com.tayyab.mobileapp.activities.AuthActivity
import com.tayyab.mobileapp.interfaces.AuthCallback
import com.tayyab.mobileapp.interfaces.VolleyCRUDCallback
import com.tayyab.mobileapp.interfaces.VolleyCallback
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection

class ShopApis(var context: Context){
    var appSettings: AppSettings=AppSettings(context)
    fun sendLoginRequest(
        username: String,
        password: String,
        authCallback: AuthCallback,
    ) {

        val paramsx = HashMap<String, String>()
        paramsx["username"] = username
        paramsx["password"] = password
        val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "ApplicationUser/Login",
            jsonObjectx,
            Response.Listener { response ->
                authCallback.onSuccess(response)

            },
            Response.ErrorListener { error ->// Do something when error occurred
                authCallback.onFailed(error)
            }
        ) {
        }

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }

    fun sendRegisterRequest(
        username: String,
        email: String,
        password: String,
        fullname: String,
        authCallback: AuthCallback
    ) {

        val paramsx = HashMap<String, String>()
        paramsx["userName"] = username
        paramsx["email"] = email
        paramsx["password"] = password
        paramsx["fullName"] = fullname
        val jsonObjectx = JSONObject(paramsx as Map<*, *>)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "ApplicationUser/Register",
            jsonObjectx,
            Response.Listener { response ->
                authCallback.onSuccess(response)

            },
            Response.ErrorListener { error ->// Do something when error occurred
                authCallback.onFailed(error)
            }
        ) {
        }

        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
     fun getCats(volleyCallback: VolleyCallback) {
        var jsonreq = ApiUtils.GsonRequestForCat<ArrayList<Category>>(
            Config.BASE_URL + "Categories",
            ArrayList(),
            null,
            { ServerResponse ->
                volleyCallback.onSuccessArrayList(ServerResponse)

            },
            { volleyError ->

                Log.e("RESP:", volleyError.toString())
            });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonreq)
    }
    fun deleteProductRequest(product: Product,volleyCallback: VolleyCRUDCallback) {
        val jsonArrayRequest = object : JsonObjectRequest(
            Method.DELETE,
            Config.BASE_URL + "Products/" + product.pr_id,
            null,
            Response.Listener { response ->

                volleyCallback.onSuccess(response)

            },
            Response.ErrorListener { error ->// Do something when error occurred
                volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                       volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
    fun updateProductRequest(product: Product,volleyCallback: VolleyCRUDCallback) {
        val jsonString = Gson().toJson(product)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Method.PUT,
            Config.BASE_URL + "Products/" + product.pr_id,
            jsonObject,
            Response.Listener { response ->
                volleyCallback.onSuccess(response)
            },
            Response.ErrorListener { error ->// Do something when error occurred
               volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                try {
                    if (response!!.data.size === 0) {
                        val responseData = "{}".toByteArray(charset("UTF8"))

                        var responsex = NetworkResponse(
                            response!!.statusCode,
                            responseData,
                            response.notModified,
                            response.networkTimeMs,
                            response.allHeaders
                        )
                        return parseNetworkResponse(responsex)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
    fun uploadBitmap(multipartBody: ByteArray, volleyCallback: VolleyCallback) {
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, Config.BASE_URL + "Products/UploadFile",
            Response.Listener { response ->
                val resultResponse = String(response.data)
                try {
                    Log.e("Messsage", resultResponse)
                    volleyCallback.onSuccess(resultResponse)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            },
            Response.ErrorListener { error ->
               volleyCallback.onError(error)
            }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["tags"] = ""
                return params
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = java.util.HashMap()
                val imagename = System.currentTimeMillis()
                params["file"] = DataPart("$imagename.png", multipartBody)
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers: java.util.HashMap<String, String> = java.util.HashMap()
                headers["Authorization"] = "Bearer " + appSettings.getToken()
                return headers
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(volleyMultipartRequest)
    }
    fun postProductRequest(product: Product,volleyCallback: VolleyCRUDCallback) {
        val jsonString = Gson().toJson(product)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Method.POST,
            Config.BASE_URL + "Products",
            jsonObject,
            Response.Listener { response ->
               volleyCallback.onSuccess(response)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
    fun postCategoryRequest(category: Category,volleyCallback: VolleyCRUDCallback) {
        val jsonString = Gson().toJson(category)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.POST,
            Config.BASE_URL + "Categories",
            jsonObject,
            Response.Listener { response ->
                volleyCallback.onSuccess(response)

            },
            Response.ErrorListener { error ->// Do something when error occurred
              volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
    fun updateCategoryRequest(category: Category,volleyCallback: VolleyCRUDCallback) {
        val jsonString = Gson().toJson(category)
        val jsonObject = JSONObject(jsonString)
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.PUT,
            Config.BASE_URL + "Categories/" + category.cat_id,
            jsonObject,
            Response.Listener { response ->
               volleyCallback.onSuccess(response)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                try {
                    if (response!!.data.size === 0) {
                        val responseData = "{}".toByteArray(charset("UTF8"))

                        var responsex = NetworkResponse(
                            response!!.statusCode,
                            responseData,
                            response.notModified,
                            response.networkTimeMs,
                            response.allHeaders
                        )
                        return parseNetworkResponse(responsex)
                    }
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                return super.parseNetworkResponse(response)
            }

            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                       volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
    fun deleteCategoryRequest(category: Category,volleyCallback: VolleyCRUDCallback) {
        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE,
            Config.BASE_URL + "Categories/" + category.cat_id,
            null,
            Response.Listener { response ->
                volleyCallback.onSuccess(response)
            },
            Response.ErrorListener { error ->// Do something when error occurred
                volleyCallback.onError(error)
            }
        ) {
            override fun parseNetworkError(volleyError: VolleyError?): VolleyError {
                if (volleyError != null) {

                    if (volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                       volleyCallback.onAuthFailed(volleyError.networkResponse.statusCode)
                    }
                }
                return super.parseNetworkError(volleyError)
            }

            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["Authorization"] = "Bearer " + appSettings.getToken()
                return params
            }
        }
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }
}