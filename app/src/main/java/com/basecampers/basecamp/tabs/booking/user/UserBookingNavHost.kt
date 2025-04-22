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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.AdminNavHost
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.tabs.booking.user.RoomType
import com.google.gson.Gson

@Composable
fun UserBookingNavHost(
    navController: NavHostController = rememberNavController(),
    bookingViewModel: UserBookingViewModel?,
    startDestination: String = "categories"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Categories Screen
        composable("categories") {
            BookingDashboardView(
                bookingViewModel = bookingViewModel,
                navBooking = { categoryId ->
                    navController.navigate("room_selection")
                }
            )
        }

        composable("room_selection") {
            RoomSelectionView(
                onNavigateBack = { navController.popBackStack() },
                onRoomSelected = { room ->
                    val roomJson = Gson().toJson(room)
                    navController.navigate("booking_review/$roomJson")
                }
            )
        }

        composable(
            route = "booking_review/{roomJson}",
            arguments = listOf(
                navArgument("roomJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomJson = backStackEntry.arguments?.getString("roomJson")
            val room = remember(roomJson) {
                Gson().fromJson(roomJson, RoomType::class.java)
            }
            
            RoomBookingReviewView(
                room = room,
                onNavigateBack = { navController.popBackStack() },
                onProceedToPayment = { room, startDate, endDate ->
                    val roomJson = Gson().toJson(room)
                    navController.navigate("payment/$roomJson/$startDate/$endDate")
                }
            )
        }

        composable(
            route = "payment/{roomJson}/{startDate}/{endDate}",
            arguments = listOf(
                navArgument("roomJson") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.LongType },
                navArgument("endDate") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val roomJson = backStackEntry.arguments?.getString("roomJson")
            val startDate = backStackEntry.arguments?.getLong("startDate") ?: 0L
            val endDate = backStackEntry.arguments?.getLong("endDate") ?: 0L
            
            val room = remember(roomJson) {
                Gson().fromJson(roomJson, RoomType::class.java)
            }
            
            RoomPaymentView(
                room = room,
                startDate = startDate,
                endDate = endDate,
                onNavigateBack = { navController.popBackStack() },
                onPaymentComplete = {
                    navController.navigate("booking_success") {
                        popUpTo("categories") { inclusive = true }
                    }
                }
            )
        }

        // Booking Success Screen
        composable("booking_success") {
            BookingSuccessView(
                onDone = {
                    navController.popBackStack(
                        route = "categories",
                        inclusive = true
                    )
                }
            )
        }
    }
}

@Composable
fun BookingSuccessView(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Success message and button implementation
        CustomButton(
            onClick = onDone,
            text = "Done",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun BookingViewPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<UserBookingViewModel>()
    
    UserBookingNavHost(
        navController = navController,
        bookingViewModel = previewViewModel
    )
}