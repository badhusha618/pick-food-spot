package com.pickfoodplace.wear.data.remote

import com.pickfoodplace.wear.BuildConfig
import com.pickfoodplace.wear.data.local.TokenStorage
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class TokenAuthenticator(private val tokenStorage: TokenStorage) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) {
            return null
        }
        val refresh = tokenStorage.getRefreshToken() ?: return null
        return try {
            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val body = JSONObject(mapOf("refreshToken" to refresh)).toString().toRequestBody(mediaType)
            val req = okhttp3.Request.Builder()
                .url(BuildConfig.BASE_URL + "auth/refresh")
                .post(body)
                .build()
            val res = client.newCall(req).execute()
            if (!res.isSuccessful) return null
            val json = res.body?.string() ?: return null
            val obj = JSONObject(json)
            val newAccess = obj.optString("token", null)
            val newRefresh = obj.optString("refreshToken", null)
            if (newAccess.isNullOrBlank() || newRefresh.isNullOrBlank()) return null
            tokenStorage.saveTokens(newAccess, newRefresh)
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccess")
                .build()
        } catch (e: Exception) {
            null
        }
    }
}