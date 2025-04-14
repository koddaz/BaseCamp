package com.basecampers.basecamp.authentication

import androidx.compose.foundation.border
import com.google.firebase.auth.ktx.auth


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.PasswordTextFieldLogin
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(authViewModel: AuthViewModel, goRegister : () -> Unit, goForgotPass : () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by authViewModel.loggedin.collectAsState()
    val loginErrorMessage by authViewModel.loginErrorMessage.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val currentUser = Firebase.auth.currentUser
            println("Logged in user: ${currentUser?.email ?: "No user logged in"}")

        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        if (loginErrorMessage.contains(AuthViewModel.LoginErrors.EMAIL_NOT_VALID) &&
            loginErrorMessage.contains(AuthViewModel.LoginErrors.PASSWORD_NOT_VALID)) {
            Text(
                text = "Incorrect email or password",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        TextField(
            label = { Text("Email") },
            value = email,
            onValueChange = {
                email = it
                if(loginErrorMessage.isNotEmpty())   {
                    authViewModel.clearLoginErrors()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    when{
                        loginErrorMessage.contains(AuthViewModel.LoginErrors.EMAIL_NOT_VALID) -> Color.Red
                        else -> Color.LightGray
                    }
                )
        )
        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextFieldLogin(
            password = password,
            onValueChange = {
                password = it
                if(loginErrorMessage.isNotEmpty()) {
                    authViewModel.clearLoginErrors()
                } },

            label = "Password",
            authViewModel = authViewModel,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            authViewModel.login(email = "admin@admin.se", password = "Test123!")
        }) {
            Text("admin@admin.se & Test123!")
        }

        Button(onClick = {
            authViewModel.login(email = "user9182@example.com", password = "Test123!")
        }) {
            Text("user9182@example.com & Test123!")
        }
        Button(
            onClick = { goForgotPass() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password")
        }

        Button(onClick = {
            authViewModel.isLoggedInTrue()
        }) {
            Text("Change isLoggedIn to True")
        }

        Button(onClick = {
            authViewModel.loginUser1()
        }) {
            Text("User 1 (User)")
        }

        Button(onClick = {
            authViewModel.loginUser2()
        }) {
            Text("User 2 (SuperUser)")
        }



        Button(onClick = {
            authViewModel.loginUser3()
        }) {
            Text("User 3 (Admin)")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { goRegister() }
        ) {
            Text("Go to Register")
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(goRegister = {}, goForgotPass = {}, authViewModel = viewModel())
}