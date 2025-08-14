package com.pickfoodplace.wear.data.remote

import com.pickfoodplace.wear.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.USE_MOCK) return chain.proceed(chain.request())
        val request = chain.request()
        val path = request.url.encodedPath
        val json = when {
            path.endsWith("/auth/login") || path.endsWith("/auth/register") || path.endsWith("/auth/refresh") ->
                "{""token"":""mock-jwt"",""refreshToken"":""mock-refresh""}"
            path.endsWith("/places") ->
                "[{""id"":""1"",""name"":""Coral Cafe"",""location"":""Downtown"",""hourlyRate"":12},{""id"":""2"",""name"":""Seaside Dine"",""location"":""Pier"",""hourlyRate"":18}]"
            path.matches(Regex(".*/places/\\w+$")) ->
                "{""id"":""1"",""name"":""Coral Cafe"",""location"":""Downtown"",""hourlyRate"":12,""amenities"": [""WiFi"",""Outdoor""],""availableSlots"": [""10:00-11:00"",""11:00-12:00""]}"
            path.endsWith("/bookings") && request.method == "GET" ->
                "[{""id"":""bk1"",""placeName"":""Coral Cafe"",""slot"":""10:00-11:00"",""qrData"":""bk1""}]"
            path.endsWith("/bookings") && request.method == "POST" ->
                "{""id"":""bk"" ,""placeName"":""Coral Cafe"",""slot"":""10:00-11:00"",""qrData"":""bk""}"
            path.matches(Regex(".*/bookings/\\w+$")) && request.method == "DELETE" ->
                "{}"
            else -> "{}"
        }
        val mediaType = "application/json".toMediaType()
        return Response.Builder()
            .code(200)
            .message(json)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(json.replace("\"\"", "\"").toResponseBody(mediaType))
            .addHeader("content-type", "application/json")
            .build()
    }
}