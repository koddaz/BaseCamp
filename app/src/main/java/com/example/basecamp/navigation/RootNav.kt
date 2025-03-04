package com.example.basecamp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.LoadingScreen
import com.example.basecamp.navigation.models.Routes


@Composable
fun RootNav() {
    val navController = rememberNavController()
    val loading: Boolean = true

    NavHost(navController = navController, startDestination = Routes.LOADING) {

        // Viewmodel för loading

        composable(Routes.LOADING) {
            LoadingScreen(goDetail = {
                if (loading == true) {
                    navController.navigate(route = Routes.LOADING)
                } else {
                    navController.navigate(route = Routes.TABNAV)
                }
            })
        }


    }
}