package com.basecampers.navigation.Tabs


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.TabScreens.BookingsTab
import com.basecampers.booking.ChooseExtras
import com.basecampers.booking.ChooseProduct
import com.basecampers.booking.SearchProduct
import com.basecampers.booking.SummaryScreen
import com.basecampers.navigation.models.Routes

object BookingRoutes {
    const val BOOKING = "booking"
    const val SEARCH = "search"
    const val PRODUCT = "product"
    const val EXTRAS = "extras"
    const val SUMMARY = "summary"
}

@Composable
fun BookingsNavigation() {
    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = BookingRoutes.BOOKING) {

        composable(route = BookingRoutes.BOOKING) {
            BookingsTab(
            )
        }
        composable(route = BookingRoutes.SEARCH) {
            SearchProduct()
        }
        composable(route = BookingRoutes.PRODUCT) {
            ChooseProduct()
        }
        composable(route = BookingRoutes.EXTRAS) {
            ChooseExtras()
        }
        composable(route = BookingRoutes.SUMMARY) {
            SummaryScreen()
        }





    }

}
