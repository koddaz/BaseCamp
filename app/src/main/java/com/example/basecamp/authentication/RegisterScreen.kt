package com.basecampers.Authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.navigation.models.LoginModel

@Composable
fun RegisterScreen(loginModel : LoginModel, goLogin : () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by loginModel.loggedin.collectAsState()
    val userInfo by loginModel.userInfo.collectAsState() // Now observing Firestore data

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            loginModel.checklogin() // Ensure user info is fetched from Firestore
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
                onClick = { loginModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { loginModel.registerAndCreateUserInFirestore(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        } else {
            val (username, userEmail) = userInfo
            val uid = loginModel.getCurrentUserUid() // Get UID from Firebase Auth

            Text("You have registered!", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Username: ${username ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${userEmail ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("UID: ${uid ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loginModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { loginModel.deleteUser() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Delete Account")
            }
            Button(
                onClick = {
                    loginModel.register(email, password)
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
    RegisterScreen(loginModel = viewModel(), goLogin = {})
}
