package com.basecampers.basecamp.tabs.booking.admin


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.booking.admin.bookingOverview.AdminCurrentBookings
import com.basecampers.basecamp.tabs.booking.admin.createBooking.AdminBookingView
import com.basecampers.basecamp.tabs.booking.admin.createBooking.AdminCategoriesView
import com.basecampers.basecamp.tabs.booking.admin.createBooking.AdminExtrasView
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel

object AdminRoutes {
    const val MAIN = "admin_main"
    const val BOOKING = "admin_booking"
    const val CATEGORY = "admin_category"
    const val EXTRA = "admin_extra"
    const val CURRENT = "current"
}

@Composable
fun AdminNavHost(
    onNavigateBack: () -> Unit = {},
) {

    val adminBookingViewModel = viewModel<AdminBookingViewModel>()
    val navController = rememberNavController()
    val scrollState = rememberScrollState()


    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        NavHost(
            modifier = Modifier.weight(1f),
            navController = navController,
            startDestination = AdminRoutes.MAIN
        ) {
            composable(AdminRoutes.MAIN) {
                AdminMainView(
                    onNavigateBack = onNavigateBack,
                    navigateCat = { navController.navigate(AdminRoutes.CATEGORY) },
                    navigateBooking = { navController.navigate(AdminRoutes.BOOKING) },
                    navigateOverview = { navController.navigate(AdminRoutes.CURRENT) }
                )
            }

            composable(AdminRoutes.CATEGORY) {
                AdminCategoriesView(
                    adminBookingViewModel = adminBookingViewModel,
                    goBack = { navController.popBackStack() },
                    navigateToBooking = { categoryId ->
                        navController.navigate(AdminRoutes.BOOKING)
                    },
                    navigateOverview = {
                        navController.navigate(AdminRoutes.CURRENT)
                    }
                )
            }

            composable(AdminRoutes.BOOKING) {
                AdminBookingView(
                    adminBookingViewModel = adminBookingViewModel,
                    goBack = { navController.popBackStack() },
                    navigateToExtra = { itemId ->
                        navController.navigate(AdminRoutes.EXTRA)
                    }
                )
            }
            composable(AdminRoutes.EXTRA) {
                AdminExtrasView(
                    adminBookingViewModel = adminBookingViewModel,
                    goBack = { navController.popBackStack() },
                    navOnConfirm = { navController.navigate(AdminRoutes.MAIN) }
                )
            }
            composable(AdminRoutes.CURRENT) {
                AdminCurrentBookings(
                )
            }
        }
    }
}