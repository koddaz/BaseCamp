package com.basecampers.basecamp.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.PasswordTextField
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel

@Composable
fun RegisterScreen(authViewModel : AuthViewModel, goLogin : () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)


            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                password = password,
                onValueChange = { password = it },
                label = "Password"
            )

            PasswordTextField(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password"
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
                onClick = { authViewModel.registerAndCreateUserInFirestore(email, password)},
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
