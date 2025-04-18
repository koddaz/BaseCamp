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
    val currentUser by authViewModel.companyProfile.collectAsState()
    val itemList: List<BookingItem> by bookingViewModel.bookingItemsList.collectAsState()

    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState()
        ?: remember { mutableStateOf<BookingItem?>(null) }
    val selectedExtraItems by bookingViewModel.selectedExtraItems.collectAsState()
    val selectedBookingItem by bookingViewModel.selectedBookingItem.collectAsState()


    val formattedDateRange by bookingViewModel.formattedDateRange.collectAsState()
    val amountOfDays by bookingViewModel.amountOfDays.collectAsState()
    val extraItems by bookingViewModel.bookingExtraList.collectAsState()
    val totalPrice by bookingViewModel.finalPrice.collectAsState()

    var isAdmin by remember { mutableStateOf(false) }



    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            bookingViewModel.setUser(user)
        }
    }

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
                        formattedDateRange = formattedDateRange,
                        selectedItem = selectedItem,
                        selectedExtraItems = selectedExtraItems,
                        bookingViewModel = bookingViewModel,
                        navBooking = {
                            navController.navigate("start")
                        },
                        totalPrice = totalPrice,
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

@Preview
@Composable
fun BookingViewPreview() {
    // Sample categories
    val dummyCategories = listOf(
        BookingCategories(
            id = "cat1",
            name = "Equipment",
            info = "Professional equipment for rent",
            createdBy = "admin"
        ),
        BookingCategories(
            id = "cat2",
            name = "Vehicles",
            info = "Cars and trucks available",
            createdBy = "admin"
        ),
        BookingCategories(
            id = "cat3",
            name = "Tools",
            info = "Hand and power tools",
            createdBy = "admin"
        )
    )

    // Sample items linked to categories
    val dummyItems = listOf(
        BookingItem(
            id = "item1",
            categoryId = "cat1",
            pricePerDay = "24.99",
            name = "Professional Camera",
            info = "High resolution DSLR camera",
            quantity = "3"
        ),
        BookingItem(
            id = "item2",
            categoryId = "cat1",
            pricePerDay = "15.99",
            name = "Drone",
            info = "4K recording capability",
            quantity = "2"
        ),
        BookingItem(
            id = "item3",
            categoryId = "cat2",
            pricePerDay = "99.99",
            name = "SUV",
            info = "All terrain vehicle with AC",
            quantity = "1"
        ),
        BookingItem(
            id = "item4",
            categoryId = "cat3",
            pricePerDay = "12.50",
            name = "Power Drill",
            info = "Cordless with extra batteries",
            quantity = "5"
        )
    )

}