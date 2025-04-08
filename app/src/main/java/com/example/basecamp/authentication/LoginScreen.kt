package com.basecampers.Authentication

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
import com.example.basecamp.components.PasswordTextField
import com.example.basecamp.components.PasswordTextFieldLogin
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.navigation.models.LoginErrors
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
        Text("Authentication", style = MaterialTheme.typography.headlineMedium)

        if (loginErrorMessage.contains(LoginErrors.EMAIL_NOT_VALID) &&
                loginErrorMessage.contains(LoginErrors.PASSWORD_NOT_VALID)) {
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
                        loginErrorMessage.contains(LoginErrors.EMAIL_NOT_VALID) -> Color.Red
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
            modifier = Modifier,
            authViewModel,
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
            Text("User 1")
        }
        
        Button(onClick = {
            authViewModel.loginUser2()
        }) {
            Text("User 2")
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