package com.example.basecamp.navigation.Tabs


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.LoadingScreen
import com.example.basecamp.TabScreens.ProfileTab
import com.example.basecamp.navigation.models.Routes


@Composable
fun ProfileNavigation() {
    val navController = rememberNavController()
    val loading: Boolean = true

    NavHost(navController = navController, startDestination = Routes.PROFILE) {



        composable(route = Routes.PROFILE) {
            ProfileTab()
        }




    }
}