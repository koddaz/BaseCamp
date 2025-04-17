package com.basecampers.basecamp.tabs.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.home.routes.HomeRoutes

@Composable
fun HomeNavHost(
    authViewModel: AuthViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoutes.HOME
    ) {
        composable(HomeRoutes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onReportClick = {
                    navController.navigate(HomeRoutes.REPORT_PROBLEM)
                }
            )
        }
        
        composable(HomeRoutes.REPORT_PROBLEM) {
            // TODO: Add ReportProblemScreen
        }
    }
}