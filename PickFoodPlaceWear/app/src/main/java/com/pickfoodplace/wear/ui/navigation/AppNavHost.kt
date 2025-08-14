package com.pickfoodplace.wear.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.pickfoodplace.wear.ui.screens.*

@Composable
fun AppNavHost() {
    val navController = rememberSwipeDismissableNavController()

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator() }
    ) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = NavRoutes.Splash,
            modifier = Modifier
        ) {
            composable(NavRoutes.Splash) {
                SplashScreen(onFinished = { isLoggedIn ->
                    if (isLoggedIn) {
                        navController.navigate(NavRoutes.Nearby) { popUpTo(NavRoutes.Splash) { inclusive = true } }
                    } else {
                        navController.navigate(NavRoutes.Login) { popUpTo(NavRoutes.Splash) { inclusive = true } }
                    }
                })
            }
            composable(NavRoutes.Login) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(NavRoutes.Nearby) { popUpTo(NavRoutes.Login) { inclusive = true } } },
                    onRegister = { navController.navigate(NavRoutes.Register) }
                )
            }
            composable(NavRoutes.Register) {
                RegisterScreen(
                    onRegistered = { navController.navigate(NavRoutes.Nearby) { popUpTo(NavRoutes.Register) { inclusive = true } } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(NavRoutes.Nearby) {
                NearbyPlacesScreen(
                    onOpenSearch = { navController.navigate(NavRoutes.Search) },
                    onOpenDetails = { placeId -> navController.navigate(NavRoutes.details(placeId)) },
                    onOpenBookings = { navController.navigate(NavRoutes.Bookings) },
                    onOpenSettings = { navController.navigate(NavRoutes.Settings) }
                )
            }
            composable(NavRoutes.Search) {
                SearchScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.Details) { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId") ?: ""
                PlaceDetailsScreen(placeId = placeId, onBooked = { bookingId ->
                    navController.navigate(NavRoutes.confirm(bookingId))
                }, onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.Confirm) { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                BookingConfirmationScreen(bookingId = bookingId, onDone = {
                    navController.navigate(NavRoutes.Bookings) { popUpTo(NavRoutes.Nearby) { inclusive = false } }
                })
            }
            composable(NavRoutes.Bookings) {
                UpcomingBookingsScreen(onBack = { navController.popBackStack() })
            }
            composable(NavRoutes.Settings) {
                SettingsScreen(onLogout = {
                    navController.navigate(NavRoutes.Login) { popUpTo(NavRoutes.Nearby) { inclusive = true } }
                }, onBack = { navController.popBackStack() })
            }
        }
    }
}