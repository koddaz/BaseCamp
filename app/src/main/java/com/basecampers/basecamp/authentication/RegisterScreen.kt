package com.basecampers.Authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.navigation.models.AuthViewModel

@Composable
fun RegisterScreen(authViewModel : AuthViewModel, goLogin : () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by authViewModel.loggedin.collectAsState()
    val userInfo by authViewModel.userInfo.collectAsState() // Now observing Firestore data

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            authViewModel.checklogin() // Ensure user info is fetched from Firestore
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        if (!isLoggedIn) {
            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                label = { Text("Password") },
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { authViewModel.registerAndCreateUserInFirestore(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
            Button(onClick = {
                goLogin()
            }) {
                Text("Go to Login")
            }
        } else {
            val (username, userEmail) = userInfo
            val uid = authViewModel.getCurrentUserUid() // Get UID from Firebase Auth

            Text("You have registered!", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Username: ${username ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${userEmail ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("UID: ${uid ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { authViewModel.deleteUser() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Account")
            }
            Button(
                onClick = {
                    authViewModel.register(email, password)
                    goLogin()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(authViewModel = viewModel(), goLogin = {})
}
