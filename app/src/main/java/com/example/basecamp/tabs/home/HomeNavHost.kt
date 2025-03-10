package com.example.basecamp.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.tabs.home.models.homeRoutes

@Composable
fun HomeNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = homeRoutes.MAIN) {
        composable(homeRoutes.MAIN) {
            HomeScreen()
        }
    }

}