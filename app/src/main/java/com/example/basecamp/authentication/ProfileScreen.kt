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
fun ProfileScreen(loginModel: LoginModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by loginModel.loggedin.collectAsState()
    val userInfo by remember { mutableStateOf(loginModel.getUserInfo()) }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val (username, email) = loginModel.getUserInfo()
            println("Logged in user: $email")
            println("Username: $username")
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)

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
            val (username, email) = userInfo

            Text("You are logged in!", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Username: ${username ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${email ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreviewScreen() {
    ProfileScreen()
}
