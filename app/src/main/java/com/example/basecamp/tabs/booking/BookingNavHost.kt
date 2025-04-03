package com.example.basecamp.tabs.booking

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Authentication.RegisterScreen
import com.example.basecamp.UserModel
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.admin.AdminBooking
import com.example.basecamp.tabs.booking.models.UserBookingViewModel
import com.example.basecamp.tabs.booking.models.bookingRoutes
import com.example.basecamp.tabs.booking.unknown.BookingCornfirmation
import com.example.basecamp.tabs.booking.user.BookingView


@Composable
fun BookingNavHost(authViewModel: AuthViewModel, user: UserModel) {
    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()

    val isLoggedIn = authViewModel.loggedin.collectAsState().value

    LaunchedEffect(isLoggedIn) {
        authViewModel.checklogin()
    }

    NavHost(navController = navController, startDestination = bookingRoutes.MAIN) {
        composable(bookingRoutes.MAIN) {
            BookingView(
                authViewModel = authViewModel,
                bookingViewModel = bookingViewModel,
                onClick = { navController.navigate(bookingRoutes.ADMIN) },
            )
        }
        composable(bookingRoutes.ADMIN) {
            AdminBooking(
                authViewModel = authViewModel,
                user = user,
                onClick = {navController.navigate(bookingRoutes.MAIN)})
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
