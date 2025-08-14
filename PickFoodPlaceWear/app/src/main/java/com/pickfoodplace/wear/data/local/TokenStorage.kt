package com.pickfoodplace.wear.data.local

import android.content.SharedPreferences

class TokenStorage(private val prefs: SharedPreferences) {
    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
    }

    fun saveTokens(access: String, refresh: String) {
        prefs.edit().putString(KEY_ACCESS, access).putString(KEY_REFRESH, refresh).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun clear() {
        prefs.edit().remove(KEY_ACCESS).remove(KEY_REFRESH).apply()
    }
}