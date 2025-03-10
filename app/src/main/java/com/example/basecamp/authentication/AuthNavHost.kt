package com.example.basecamp.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.LoginScreen
import com.example.basecamp.authentication.models.authRoutes
import com.example.basecamp.tabs.home.HomeScreen
import com.example.basecamp.tabs.profile.models.profileRoutes

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = authRoutes.MAIN) {
        composable(profileRoutes.MAIN) {
            LoginScreen()
        }
    }

}