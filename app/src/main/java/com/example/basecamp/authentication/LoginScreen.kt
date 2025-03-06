package com.basecampers.Authentication
import com.google.firebase.auth.ktx.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.navigation.models.LoginModel
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(loginModel: LoginModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by loginModel.loggedin.collectAsState()


    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val currentUser = Firebase.auth.currentUser
            println("Logged in user: ${currentUser?.email ?: "No user logged in"}")

        }
    }




    Column(modifier = Modifier.padding(16.dp)) {
        Text("Authentication", style = MaterialTheme.typography.headlineMedium)

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
                onClick = { loginModel.register(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        } else {

            Text("You are logged in!", style = MaterialTheme.typography.bodyLarge)
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
fun PreviewLoginScreen() {
    LoginScreen()
}