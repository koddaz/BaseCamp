package com.example.basecamp.tabs.booking

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.booking.BookingScreen
import com.example.basecamp.tabs.booking.models.bookingRoutes


@Composable
fun BookingNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = bookingRoutes.MAIN) {
        composable(bookingRoutes.MAIN) {
            BookingScreen()
        }
    }

}