package com.pickfoodplace.wear.data.remote.dto

import com.squareup.moshi.Json

data class AuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class AuthResponse(
    @Json(name = "token") val token: String,
    @Json(name = "refreshToken") val refreshToken: String
)

data class PlaceResponse(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "location") val location: String,
    @Json(name = "hourlyRate") val hourlyRate: Int
)

data class PlaceDetailsResponse(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "location") val location: String,
    @Json(name = "hourlyRate") val hourlyRate: Int,
    @Json(name = "amenities") val amenities: List<String>,
    @Json(name = "availableSlots") val availableSlots: List<String>
)

data class BookingCreateRequest(
    @Json(name = "placeId") val placeId: String,
    @Json(name = "slot") val slot: String
)

data class BookingResponse(
    @Json(name = "id") val id: String,
    @Json(name = "placeName") val placeName: String,
    @Json(name = "slot") val slot: String,
    @Json(name = "qrData") val qrData: String
)