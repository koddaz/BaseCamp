package com.basecampers.basecamp.tabs.booking.admin.createBooking


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
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel

object AdminRoutes {
    const val MAIN = "admin_main"
    const val BOOKING = "admin_booking"
    const val CATEGORY = "admin_category"
    const val EXTRA = "admin_extra"
}

@Composable
fun AdminNavHost(authViewModel: AuthViewModel = viewModel(), changeView: () -> Unit) {
    val adminBookingViewModel = viewModel<AdminBookingViewModel>()
    val navController = rememberNavController()
    val userInfo by authViewModel.companyProfile.collectAsState()
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
                AdminCategoriesView(
                    adminBookingViewModel = adminBookingViewModel,
                    authViewModel = authViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    navigateToBooking = { categoryId ->
                        navController.navigate(AdminRoutes.BOOKING)
                    }
                )
            }
            composable(AdminRoutes.BOOKING) {
                AdminBookingView(
                    adminBookingViewModel = adminBookingViewModel,
                    userInfo = userInfo,
                    goBack = { navController.popBackStack() },
                    navigateToExtra = { itemId ->
                        navController.navigate(AdminRoutes.EXTRA) }
            )
            }
            composable(AdminRoutes.EXTRA) {
                AdminExtrasView(
                    adminBookingViewModel = adminBookingViewModel,
                    goBack = { navController.popBackStack() },
                    navOnConfirm = { navController.navigate(AdminRoutes.MAIN) }
                )
            }
        }
    }
}