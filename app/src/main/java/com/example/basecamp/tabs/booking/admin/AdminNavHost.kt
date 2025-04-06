package com.example.basecamp.tabs.booking.admin

import AdminExtrasView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel

object AdminRoutes {
    const val MAIN = "admin_main"
    const val BOOKING = "admin_booking"
    const val CATEGORY = "admin_category"
    const val EXTRA = "admin_extra"
    const val BOOKING_WITH_CATEGORY = "admin_booking/{categoryId}"
    const val EXTRA_WITH_BOOKING = "admin_extra/{categoryId}/{bookingName}/{bookingInfo}/{bookingPrice}/{bookingId}"
}

@Composable
fun AdminNavHost(authViewModel: AuthViewModel = viewModel()) {
    val adminBookingViewModel = viewModel<AdminBookingViewModel>()
    val navController = rememberNavController()
    val userInfo by authViewModel.currentUser.collectAsState()
    val categories by adminBookingViewModel.categories.collectAsState()

    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            adminBookingViewModel.setUser(user)
        }
        categories.let {
            adminBookingViewModel.retrieveCategories()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NavHost(
            modifier = Modifier.weight(1f),
            navController = navController,
            startDestination = AdminRoutes.MAIN
        ) {
            composable(AdminRoutes.MAIN) {
                AdminMainView(
                    adminBookingViewModel = adminBookingViewModel,
                    navigateCat = { navController.navigate(AdminRoutes.CATEGORY) },
                    navigateBooking = { navController.navigate(AdminRoutes.BOOKING) },
                    navigateExtra = { navController.navigate(AdminRoutes.EXTRA) },
                    userInfo = userInfo,
                )
            }
            composable(AdminRoutes.BOOKING) {
                AdminBookingView(
                    authViewModel = authViewModel,
                    adminBookingViewModel = adminBookingViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    navigateToExtra = { categoryId, bookingName, bookingInfo, bookingPrice, bookingId ->
                        navController.navigate("admin_extra/$categoryId/$bookingName/$bookingInfo/$bookingPrice/$bookingId")
                    }
                )
            }
            composable(AdminRoutes.EXTRA) {
                AdminExtrasView(
                    adminBookingViewModel = adminBookingViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() })
            }
            composable(AdminRoutes.CATEGORY) {
                AdminCategoriesView(
                    adminBookingViewModel = adminBookingViewModel,
                    authViewModel = authViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    navigateToBooking = { categoryId ->
                        navController.navigate("admin_booking/$categoryId")
                    }
                )
            }
            composable(AdminRoutes.BOOKING_WITH_CATEGORY) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                AdminBookingView(
                    authViewModel = authViewModel,
                    adminBookingViewModel = adminBookingViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    navigateToExtra = { categoryId, bookingName, bookingInfo, bookingPrice, bookingId ->
                        navController.navigate("admin_extra/$categoryId/$bookingName/$bookingInfo/$bookingPrice/$bookingId")
                    },
                    categoryId = categoryId
                )
            }

            composable(AdminRoutes.EXTRA_WITH_BOOKING) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                val bookingName = backStackEntry.arguments?.getString("bookingName") ?: ""
                val bookingInfo = backStackEntry.arguments?.getString("bookingInfo") ?: ""
                val bookingPrice = backStackEntry.arguments?.getString("bookingPrice") ?: ""
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""

                AdminExtrasView(
                    adminBookingViewModel = adminBookingViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    categoryId = categoryId,
                    bookingName = bookingName,
                    bookingInfo = bookingInfo,
                    bookingPrice = bookingPrice,
                    bookingId = bookingId

                )
            }
        }
    }
}