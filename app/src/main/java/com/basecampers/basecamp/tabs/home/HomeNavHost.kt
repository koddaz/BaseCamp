package com.basecampers.basecamp.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyViewModel
import com.basecampers.basecamp.tabs.home.models.homeRoutes

@Composable
fun HomeNavHost(authViewModel : AuthViewModel, companyViewModel: CompanyViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = homeRoutes.MAIN) {
        composable(homeRoutes.MAIN) {
            HomeScreen(authViewModel, companyViewModel)
        }
    }

}