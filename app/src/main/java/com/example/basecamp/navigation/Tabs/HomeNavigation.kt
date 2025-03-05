package com.basecampers.navigation.Tabs


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.TabScreens.HomeTab
import com.basecampers.navigation.models.Routes


@Composable
fun HomeNavigation() {
    val navController = rememberNavController()
    val loading: Boolean = true

    NavHost(navController = navController, startDestination = Routes.HOME) {



        composable(route = Routes.HOME) {
            HomeTab()
        }



    }
}