package com.basecampers.basecamp.aRootFolder

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
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
import com.basecampers.basecamp.navigation.models.AuthViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Root(modifier: Modifier = Modifier, authViewModel : AuthViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(true) }
    val tempFunction = { isLoading = false }

    val isLoggedin by authViewModel.loggedin.collectAsState()

        Column(modifier.fillMaxSize()) {
            if (isLoading) {
                LoadingScreen(
                    tempFunction = tempFunction,
                )
            } else if (isLoggedin) {
                TabNavigation(authViewModel)
            } else {
                AuthNavHost(authViewModel)
            }
        }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    Root(authViewModel = viewModel())
}