package com.tayyab.mobileapp.utils

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.tayyab.mobileapp.models.Category
import com.tayyab.mobileapp.models.Product
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class ApiUtils {
    class GsonRequest<T>(
        url: String,
        private val clazz: ArrayList<T>,
        private val headers: MutableMap<String, String>?,
        private val listener: Response.Listener<T>,
        errorListener: Response.ErrorListener
    ) : Request<T>(Method.GET, url, errorListener) {
        private val gson = Gson()
        override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()
        override fun deliverResponse(response: T) = listener.onResponse(response)
        override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
            return try {
//                val yourArray: ArrayList<Product> = Gson().fromJson(
//                    response.toString(),
//                    object : TypeToken<List<Product?>?>() {}.type
//                )

                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
                Response.success(
                    gson.fromJson(json,  object : TypeToken<List<Product?>?>() {}.type),
                    HttpHeaderParser.parseCacheHeaders(response))
            } catch (e: UnsupportedEncodingException) {
                Response.error(ParseError(e))
            } catch (e: JsonSyntaxException) {
                Response.error(ParseError(e))
            }
        }
        inline fun <reified T> get(str:String ): ArrayList<T>{

            // val str = "[{},{}]"

            val type = TypeToken.getParameterized(ArrayList::class.java, T::class.java).type

            val t = Gson().fromJson<ArrayList<T>>(str, type)


            return t
        }
    }
    class GsonRequestForCat<T>(
        url: String,
        private val clazz: ArrayList<T>,
        private val headers: MutableMap<String, String>?,
        private val listener: Response.Listener<T>,
        errorListener: Response.ErrorListener
    ) : Request<T>(Method.GET, url, errorListener) {
        private val gson = Gson()
        override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()
        override fun deliverResponse(response: T) = listener.onResponse(response)
        override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
            return try {
//                val yourArray: ArrayList<Product> = Gson().fromJson(
//                    response.toString(),
//                    object : TypeToken<List<Product?>?>() {}.type
//                )

                val json = String(
                    response?.data ?: ByteArray(0),
                    Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
                Response.success(
                    gson.fromJson(json,  object : TypeToken<List<Category?>?>() {}.type),
                    HttpHeaderParser.parseCacheHeaders(response))
            } catch (e: UnsupportedEncodingException) {
                Response.error(ParseError(e))
            } catch (e: JsonSyntaxException) {
                Response.error(ParseError(e))
            }
        }
        inline fun <reified T> get(str:String ): ArrayList<T>{

            // val str = "[{},{}]"

            val type = TypeToken.getParameterized(ArrayList::class.java, T::class.java).type

            val t = Gson().fromJson<ArrayList<T>>(str, type)


            return t
        }
    }
}