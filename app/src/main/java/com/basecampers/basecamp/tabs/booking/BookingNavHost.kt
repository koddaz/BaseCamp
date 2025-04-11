package com.basecampers.basecamp.tabs.booking

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.RegisterScreen
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.booking.admin.AdminNavHost
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.bookingRoutes
import com.basecampers.basecamp.tabs.booking.unknown.BookingCornfirmation
import com.basecampers.basecamp.tabs.booking.user.BookingView

@Composable
fun BookingNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()
    val adminBookingViewModel: AdminBookingViewModel = viewModel()
    val userInfo by authViewModel.currentUser.collectAsState()

    val isLoggedIn = authViewModel.loggedin.collectAsState().value

    LaunchedEffect(isLoggedIn) {
        authViewModel.checkLoggedin()
    }

    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            adminBookingViewModel.setUser(user)
        }
    }

    NavHost(navController = navController, startDestination = bookingRoutes.ADMIN) {
        composable(bookingRoutes.MAIN) {

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
