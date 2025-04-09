package com.basecampers.basecamp.tabs.profile

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.profile.models.profileRoutes

@Composable
fun ProfileNavHost(authViewModel : AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = profileRoutes.MAIN) {
        composable(profileRoutes.MAIN) {
            ProfileScreen(authViewModel)
        }
    }

}