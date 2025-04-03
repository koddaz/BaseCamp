package com.example.basecamp.tabs.social

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.profile.ProfileScreen
import com.example.basecamp.tabs.social.models.socialRoutes


@Composable
fun SocialNavHost(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = socialRoutes.MAIN) {
        composable(socialRoutes.MAIN) {
            ProfileScreen(
                authViewModel = authViewModel
            )
        }
    }

}