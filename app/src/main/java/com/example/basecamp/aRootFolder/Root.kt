package com.example.basecamp.aRootFolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.navigation.TabNavigation
import com.example.basecamp.UserViewModel
import com.example.basecamp.authentication.AuthNavHost
import com.example.basecamp.navigation.models.AuthViewModel

@Composable
fun Root(authViewModel : AuthViewModel = viewModel(), userViewModel : UserViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(true) }
    val tempFunction = { isLoading = false }

    val isLoggedIn by authViewModel.loggedin.collectAsState()

    Column(Modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingScreen(
                tempFunction = tempFunction,
                isLoggedIn = isLoggedIn
            )
        } else if(isLoggedIn) {
            TabNavigation(authViewModel)
        } else {
            AuthNavHost(authViewModel, userViewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    Root(authViewModel = viewModel())
}