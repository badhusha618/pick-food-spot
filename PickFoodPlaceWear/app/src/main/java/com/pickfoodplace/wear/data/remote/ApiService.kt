package com.pickfoodplace.wear.data.remote

import com.pickfoodplace.wear.data.remote.dto.AuthRequest
import com.pickfoodplace.wear.data.remote.dto.AuthResponse
import com.pickfoodplace.wear.data.remote.dto.BookingCreateRequest
import com.pickfoodplace.wear.data.remote.dto.BookingResponse
import com.pickfoodplace.wear.data.remote.dto.PlaceDetailsResponse
import com.pickfoodplace.wear.data.remote.dto.PlaceResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @GET("places")
    suspend fun getPlaces(
        @Query("lat") lat: Double?,
        @Query("lng") lng: Double?,
        @Query("minPrice") minPrice: Int?,
        @Query("maxPrice") maxPrice: Int?,
        @Query("q") query: String?
    ): List<PlaceResponse>

    @GET("places/{id}")
    suspend fun getPlaceDetails(@Path("id") id: String): PlaceDetailsResponse

    @POST("bookings")
    suspend fun createBooking(@Body request: BookingCreateRequest): BookingResponse

    @GET("bookings")
    suspend fun getBookings(): List<BookingResponse>

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: String)
}