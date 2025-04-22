package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.AdminNavHost
import com.basecampers.basecamp.tabs.booking.user.bookingOverview.UserCurrentBookings
import com.basecampers.basecamp.tabs.booking.user.bookingOverview.UserEditBookingView
import com.basecampers.basecamp.tabs.booking.user.createBooking.UserBookingMainView
import com.basecampers.basecamp.tabs.booking.user.createBooking.UserCategoryView
import com.basecampers.basecamp.tabs.booking.user.createBooking.UserConfirmationView
import com.basecampers.basecamp.tabs.booking.user.createBooking.UserExtraItem
import com.basecampers.basecamp.tabs.booking.user.createBooking.UserItemView

import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserBookingNavHost() {

    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()


    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            NavHost(
                navController = navController,
                startDestination = "start",
                modifier = Modifier.weight(1f)
            ) {
                composable("start") {
                    UserBookingMainView(
                        navToBooking = {
                            navController.navigate("categoryView")
                        },
                        navToCurrentBookings = {
                            navController.navigate("currentBookings")
                        }
                    )
                }

                composable("categoryView") {
                    UserCategoryView(
                        bookingViewModel = bookingViewModel,
                        navBooking = { categoryId ->
                            navController.navigate("itemView")
                        },
                    )
                }

                composable("itemView") {
                    UserItemView(
                        bookingViewModel = bookingViewModel,
                        navExtra = { itemId ->
                            navController.navigate("extrasView")
                        }
                    )
                }

                composable("extrasView") {
                    UserExtraItem(
                        bookingViewModel = bookingViewModel,
                        navBooking = {
                            navController.navigate("confirmationView")
                        }
                    )
                }

                composable("confirmationView") {
                    UserConfirmationView(
                        bookingViewModel = bookingViewModel,
                        navBooking = {
                            navController.navigate("start")
                        },
                    )
                }
                composable("editBooking") {
                    UserEditBookingView(
                        bookingViewModel = bookingViewModel,
                        goBack = {
                            navController.popBackStack()
                        },
                        navConfirm = {
                            navController.navigate("currentBookings")
                        }
                    )
                }
                composable("currentBookings") {
                    UserCurrentBookings(
                        bookingViewModel = bookingViewModel,
                        goBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }


