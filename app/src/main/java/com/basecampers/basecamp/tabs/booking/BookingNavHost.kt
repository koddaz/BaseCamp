package com.basecampers.basecamp.tabs.booking

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.RegisterScreen
import com.basecampers.basecamp.navigation.models.AuthViewModel
import com.basecampers.basecamp.tabs.booking.admin.AdminBooking
import com.basecampers.basecamp.tabs.booking.models.BookingViewModel
import com.basecampers.basecamp.tabs.booking.models.bookingRoutes
import com.basecampers.basecamp.tabs.booking.unknown.BookingCornfirmation
import com.basecampers.basecamp.tabs.booking.unknown.BookingExtra
import com.basecampers.basecamp.tabs.booking.user.BookingView


@Composable
fun BookingNavHost() {
    val authViewModel: AuthViewModel = viewModel()
    val navController = rememberNavController()
    val bookingViewModel: BookingViewModel = viewModel()

    val isLoggedIn = authViewModel.loggedin.collectAsState().value

    LaunchedEffect(isLoggedIn) {
        authViewModel.checklogin()
    }

    NavHost(navController = navController, startDestination = bookingRoutes.MAIN) {
        composable(bookingRoutes.MAIN) {
            BookingView(
                bookingViewModel = bookingViewModel,
                onClick = { navController.navigate(bookingRoutes.ADMIN) },
            )
           /* BookingScreen(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(bookingRoutes.EXTRA) })

            */
        }
        composable(bookingRoutes.EXTRA) {
            BookingExtra(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(bookingRoutes.CONFIRMATION) }
            )
        }
        composable(bookingRoutes.ADMIN) {
            AdminBooking(onClick = {navController.navigate(bookingRoutes.MAIN)})
        }
        composable(bookingRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                goLogin = {
                    // Navigate back to confirmation and finnish the booking!
                }
            )
        }
        composable(bookingRoutes.CONFIRMATION) {
            BookingCornfirmation(
                bookingViewModel = bookingViewModel,
                onBack = { navController.popBackStack() },
                onComplete = {
                    if (isLoggedIn) {
                        bookingViewModel.createBooking(
                            onSuccess = {
                                Toast.makeText(
                                    navController.context,
                                    "Booking confirmed!",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate(bookingRoutes.MAIN)
                            },
                            onFailure = { error ->
                                Toast.makeText(
                                    navController.context,
                                    "Error: ${error.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    } else {
                        navController.navigate(bookingRoutes.REGISTER)
                    }
                }
            )

        }
    }

}
