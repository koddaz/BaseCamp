package com.example.basecamp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.LoadingScreen
import com.example.basecamp.navigation.Tabs.BookingsNavigation
import com.example.basecamp.navigation.Tabs.HomeNavigation
import com.example.basecamp.navigation.Tabs.ProfileNavigation
import com.example.basecamp.navigation.Tabs.SocialNavigation
import com.example.basecamp.navigation.models.Routes


@Composable
fun TabNavigation() {
     val navController = rememberNavController()


    NavHost(navController = navController, startDestination = Routes.HOME) {



        composable(route = Routes.HOME) {
            HomeNavigation()
        }

        composable(route = Routes.BOOKING) {
            BookingsNavigation()
        }

        composable(route = Routes.PROFILE) {
            ProfileNavigation()
        }

        composable(route = Routes.SOCIAL) {
            SocialNavigation()
        }




    }
}