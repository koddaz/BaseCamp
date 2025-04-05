package com.example.basecamp.tabs.booking

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Authentication.RegisterScreen
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.admin.AdminMainView
import com.example.basecamp.tabs.booking.admin.AdminNavHost
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
import com.example.basecamp.tabs.booking.models.UserBookingViewModel
import com.example.basecamp.tabs.booking.models.bookingRoutes
import com.example.basecamp.tabs.booking.unknown.BookingCornfirmation
import com.example.basecamp.tabs.booking.user.BookingView


@Composable
fun BookingNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()
    val adminBookingViewModel: AdminBookingViewModel = viewModel()
    val userInfo by authViewModel.currentUser.collectAsState()

    val isLoggedIn = authViewModel.loggedin.collectAsState().value

    LaunchedEffect(isLoggedIn) {
        authViewModel.checklogin()
    }

    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            adminBookingViewModel.setUser(user)
        }
    }

    NavHost(navController = navController, startDestination = bookingRoutes.ADMIN) {
        composable(bookingRoutes.MAIN) {
            BookingView(
                userInfo = userInfo,
                authViewModel = authViewModel,
                bookingViewModel = bookingViewModel,
                onClick = { navController.navigate(bookingRoutes.ADMIN) },
            )
        }
        composable(bookingRoutes.ADMIN) {
            AdminNavHost(
                authViewModel = authViewModel

            )
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
