package com.basecampers.basecamp.tabs.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel

@Composable
fun HomeScreen(authViewModel: AuthViewModel) {
    val userInfo by authViewModel.companyProfile.collectAsState()


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

        Button(onClick = {
            authViewModel.registerUserToCompany(
                companyId = "66a2bdbb-7218-48a3-ab86-4d1bd2de0728"
            )
        }) {
            Text("Register User to Company --> TEST FUNKAR BARA FÖR ETT FÖRETAG")
        }
    }
}