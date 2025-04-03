package com.example.basecamp.tabs.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.profile.models.ProfileViewModel

import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel = viewModel()) {

    val userInfo by authViewModel.userInfo.collectAsState()
    val uid = authViewModel.getCurrentUserUid()

    val profile by profileViewModel.profile.collectAsState()

    LaunchedEffect(uid) {
        uid?.let {
            profileViewModel.refreshProfile(it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("You are logged in!", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Username: ${profile?.name ?: userInfo.first ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        Text("Email: ${profile?.email ?: userInfo.second ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        Text("UID: ${uid ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.deleteUser() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreviewScreen() {
    ProfileScreen(authViewModel = viewModel())
}
