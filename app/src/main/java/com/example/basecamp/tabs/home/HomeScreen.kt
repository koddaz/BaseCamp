package com.example.basecamp.tabs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.basecamp.navigation.models.LoginModel

@Composable
fun HomeScreen(loginModel: LoginModel) {
    Column(verticalArrangement = Arrangement.Center) {
        Text("Home Screen", style = MaterialTheme.typography.titleLarge)
        Button(onClick = {
            loginModel.logout()
        }) {
            Text("Logout")
        }

        Button(onClick = {
            loginModel.isLoggedInFalse()
        }) {
            Text("Change isLoggedIn to False")
        }
    }
}