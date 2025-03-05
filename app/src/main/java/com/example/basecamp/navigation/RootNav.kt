package com.basecampers.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.LoadingScreen
import com.basecampers.navigation.models.Routes


@Composable
fun RootNav(modifier: Modifier) {
    val navController = rememberNavController()
    // var loading: Boolean by remember { mutableStateOf(true) }




    NavHost(navController = navController, startDestination = Routes.LOADING) {
        composable(Routes.LOADING) {
            LoadingScreen(
                tempFunction = {
                    navController.navigate(Routes.TABNAV)
                }
            )
        }
        composable(Routes.TABNAV) {
            TabNavigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RootNavPreview() {
    RootNav(modifier = Modifier)
}