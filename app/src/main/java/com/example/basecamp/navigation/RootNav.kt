package com.basecampers.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.LoadingScreen
import com.basecampers.navigation.models.Routes


@Composable
fun RootNav() {
    val modifier: Modifier = Modifier
    val navController = rememberNavController()
    val innerPadding = 20.dp

    // om loading är klar ska navController navigera till TabNavigation

    Column(modifier.padding(top = innerPadding)) {
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
}




@Preview(showBackground = true)
@Composable
fun RootNavPreview() {
    RootNav()
}