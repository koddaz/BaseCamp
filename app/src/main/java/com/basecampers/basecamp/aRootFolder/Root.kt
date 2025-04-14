package com.basecampers.basecamp.aRootFolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.navigation.TabNavigation
import com.basecampers.basecamp.authentication.AuthNavHost
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun Root(authViewModel : AuthViewModel = viewModel(), innerPadding: PaddingValues) {
    var isLoading by remember { mutableStateOf(true) }
    val tempFunction = { isLoading = false }

    val isLoggedIn by authViewModel.loggedin.collectAsState()

    LaunchedEffect(Unit) {
        initializeAppSession(
            authViewModel = authViewModel,
            onComplete = { isLoading = false }
        )
    }
    
    Column(Modifier.fillMaxSize().padding(innerPadding)) {
        if (isLoading) {
            LoadingScreen(
                tempFunction = tempFunction,
                isLoggedIn = isLoggedIn
            )
        } else if(isLoggedIn) {
            TabNavigation(authViewModel)
        } else {
            AuthNavHost(authViewModel)
        }
    }
}

private suspend fun initializeAppSession(
    authViewModel: AuthViewModel,
    onComplete: () -> Unit
) {
    authViewModel.checkLoggedin()

    if (authViewModel.loggedin.value) {
        val userId = authViewModel.getCurrentUserUid()
        userId?.let {
            authViewModel.fetchUserInfoFromFirestore(it)
            authViewModel.fetchCurrentUserModel()
            // Check status of user in company (admin? SuperUser?)
            // load company specific data (Categories, Items)
        }
    }

    delay(1500) //Temporary delay to simulate loading, vi tar bort denna när vi har lagt till
    // alla funktioner som ska köras på appstart.

    onComplete()
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    Root(authViewModel = viewModel(), innerPadding = PaddingValues())
}