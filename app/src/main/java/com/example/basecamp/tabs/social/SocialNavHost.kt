package com.example.basecamp.tabs.social

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.tabs.home.HomeScreen
import com.example.basecamp.tabs.social.models.socialRoutes


@Composable
fun SocialNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = socialRoutes.MAIN) {
        composable(socialRoutes.MAIN) {
            HomeScreen()
        }
    }

}