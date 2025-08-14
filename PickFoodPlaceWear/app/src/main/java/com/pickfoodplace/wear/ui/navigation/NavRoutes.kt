package com.pickfoodplace.wear.ui.navigation

object NavRoutes {
    const val Splash = "splash"
    const val Login = "login"
    const val Register = "register"
    const val Nearby = "nearby"
    const val Search = "search"
    const val Details = "details/{placeId}"
    const val Confirm = "confirm/{bookingId}"
    const val Bookings = "bookings"
    const val Settings = "settings"

    fun details(placeId: String) = "details/$placeId"
    fun confirm(bookingId: String) = "confirm/$bookingId"
}