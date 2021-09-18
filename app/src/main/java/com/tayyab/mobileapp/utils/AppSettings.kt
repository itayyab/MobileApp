package com.tayyab.mobileapp.utils

import android.content.Context
import android.content.SharedPreferences

class AppSettings(// Context
    private val _context: Context
) {
    private val pref: SharedPreferences

    companion object {
        // User name (make variable public to access from outside)
        private const val KEY_TOKEN = "logintoken"
        private const val PREF_NAME = "MASettings"
        private const val KEY_LOGGEDIN = "loggedin"
        private const val KEY_ISADMIN = "isAdmin"
    }

    // Constructor
    init {
        // Shared pref mode
        pref = _context.getSharedPreferences(PREF_NAME, 0)
    }

    fun saveToken(text: String?) {
        val editor = pref.edit()
        editor.putString(KEY_TOKEN, text)
        editor.apply()
    }

    fun getToken(): String? {
        return pref.getString(KEY_TOKEN, "Default")
    }

    fun saveLoggedIn(first: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(KEY_LOGGEDIN, first)
        editor.apply()
    }

    fun getLoggedIn(): Boolean {
        return pref.getBoolean(KEY_LOGGEDIN, false)
    }

    fun saveIsAdmin(first: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(KEY_ISADMIN, first)
        editor.apply()
    }

    fun getIsAdmin(): Boolean {
        return pref.getBoolean(KEY_ISADMIN, false)
    }
}