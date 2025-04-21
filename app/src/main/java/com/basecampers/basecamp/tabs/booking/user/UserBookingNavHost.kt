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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.createBooking.AdminNavHost
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem

import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserBookingNavHost(authViewModel: AuthViewModel) {

    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()
    val categoryList by bookingViewModel.categoriesList.collectAsState()
    var isAdmin by remember { mutableStateOf(false) }



    // Fix the category/item loading
    LaunchedEffect(Unit) {
        bookingViewModel.retrieveCategories()
    }

    // Separate LaunchedEffect to react when categories change
    LaunchedEffect(categoryList) {
        if (categoryList.isNotEmpty()) {
            categoryList.forEach { category ->
                bookingViewModel.retrieveBookingItems(category.id)
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        if (isAdmin) {
            Column(modifier = Modifier.weight(1f)) {
                AdminNavHost(authViewModel = authViewModel, changeView = { isAdmin = false })
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = "start",
                modifier = Modifier.weight(1f)
            ) {
                composable("start") {
                    TESTVIEW(
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
        Row(modifier = Modifier.fillMaxWidth()) {
            CustomButton(text = "Admin", onClick = { isAdmin = !isAdmin })
            CustomButton(text = "User", onClick = { isAdmin = false })
        }
    }
}

@Composable
fun TESTVIEW(
    navToBooking: () -> Unit,
    navToCurrentBookings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomButton(
            text = "Book an item",
            onClick = {
                navToBooking()
            }
        )
        CustomButton(
            text = "See current bookings",
            onClick = {
                navToCurrentBookings()
            }
        )
    }
}
