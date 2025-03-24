package com.example.basecamp.tabs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.basecamp.navigation.models.AuthViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel) {
    Column(verticalArrangement = Arrangement.Center) {
        Text("Home Screen", style = MaterialTheme.typography.titleLarge)
        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Logout")
        }

        Button(onClick = {
            authViewModel.isLoggedInFalse()
        }) {
            Text("Change isLoggedIn to False")
        }
    }
}