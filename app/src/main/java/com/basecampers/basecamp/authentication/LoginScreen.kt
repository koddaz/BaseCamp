
package com.basecampers.basecamp.authentication

import com.google.firebase.auth.ktx.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.booking.user.FIREBASETESTSTUFF
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(authViewModel: AuthViewModel, goRegister : () -> Unit, goForgotPass : () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by authViewModel.loggedin.collectAsState()
    
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val currentUser = Firebase.auth.currentUser
            println("Logged in user: ${currentUser?.email ?: "No user logged in"}")            
        }
    }
    
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Authentication", style = MaterialTheme.typography.headlineMedium)
        
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

        FIREBASETESTSTUFF(
            authViewModel = authViewModel
        )

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