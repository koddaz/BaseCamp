package com.basecampers.navigation.Tabs

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.TabScreens.SocialTab
import com.basecampers.navigation.models.Routes


@Composable
fun SocialNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.SOCIAL) {

        composable(route = Routes.SOCIAL) {
            SocialTab()
        }




    }
}