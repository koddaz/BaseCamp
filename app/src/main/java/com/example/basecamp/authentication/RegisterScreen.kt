package com.basecampers.Authentication

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.components.PasswordTextField
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.navigation.models.RegisterErrors

@Composable
fun RegisterScreen(authViewModel : AuthViewModel, goLogin : () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val errorMessage by authViewModel.registerErrorMessage.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if(errorMessage.contains(RegisterErrors.EMAIL)) Color.Red else Color.Transparent)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                password = password,
                onValueChange = { password = it },
                label = "Password",
                modifier = Modifier
                    .border(1.dp, if(errorMessage.contains(RegisterErrors.PASSWORD)) Color.Red else Color.Transparent),
                authViewModel
            )

            PasswordTextField(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                modifier = Modifier
                    .border(1.dp, if(errorMessage.contains(RegisterErrors.CONFIRM_PASSWORD)) Color.Red else Color.Transparent),
                authViewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { authViewModel.registerAndCreateUserInFirestore(email, password, confirmPassword) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
            Button(onClick = {
                goLogin()
            }) {
                Text("Go to Login")
            }

    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(authViewModel = viewModel(), goLogin = {})
}