package com.tayyab.mobileapp.utils

import android.content.Context
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class AuthUtils(var context: Context) {
    var appSettings: AppSettings = AppSettings(context)
    fun getUserID(): String {
        val extract = appSettings.getToken()?.split(".")?.get(1)
        val decodedBytes = Base64.decode(extract, Base64.DEFAULT)
        val decodedString = String(decodedBytes)
        val javaRootMapObject: Map<*, *> = Gson().fromJson(
            decodedString,
            Map::class.java
        )
        return javaRootMapObject.get("UserID").toString()
    }

    fun getTokenExp(): String {
        val extract = appSettings.getToken()?.split(".")?.get(1)
        val decodedBytes = Base64.decode(extract, Base64.DEFAULT)
        val decodedString = String(decodedBytes)
        val mapType: Type =
            object : TypeToken<Map<String?, String?>>() {}.type
        val javaRootMapObject: Map<String, String> = Gson().fromJson(decodedString, mapType)
        return javaRootMapObject.get("exp").toString()
    }
}