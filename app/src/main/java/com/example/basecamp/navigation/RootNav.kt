package com.example.basecamp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.LoadingScreen
import com.example.basecamp.TESTFILES.TabNavigation
import com.example.basecamp.navigation.models.Routes
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun RootNav(modifier: Modifier) {
    val navController = rememberNavController()
    var loading: Boolean by remember { mutableStateOf(true) }
    val tempFunction: () -> Unit = {
        loading = false
    }

    NavHost(navController = navController, startDestination = if (loading) Routes.LOADING else Routes.TABNAV) {
        composable(Routes.LOADING) {
            LoadingScreen(tempFunction = tempFunction)
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