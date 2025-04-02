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
import com.example.basecamp.UserViewModel
import com.example.basecamp.components.PasswordTextField
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.navigation.models.RegisterErrors

@Composable
fun RegisterScreen(authViewModel : AuthViewModel, goLogin : () -> Unit, userViewModel : UserViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }

    val errorMessage by authViewModel.registerErrorMessage.collectAsState()
    val hasEmailError = errorMessage.any { it in listOf(
        RegisterErrors.EMAIL_EMPTY,
        RegisterErrors.EMAIL_NO_AT,
        RegisterErrors.EMAIL_NO_DOT)
    }

    val hasPasswordError = errorMessage.any { it in listOf(
        RegisterErrors.PASSWORD_EMPTY,
        RegisterErrors.PASSWORD_TOO_SHORT,
        RegisterErrors.PASSWORD_NO_SPECIAL_CHAR,
        RegisterErrors.PASSWORD_NO_UPPERCASE,
        RegisterErrors.PASSWORD_NO_NUMBER
    ) }

    val hasConfirmPasswordError = errorMessage.any { it in listOf(
        RegisterErrors.CONFIRM_PASSWORD_EMPTY,
        RegisterErrors.CONFIRM_PASSWORD_MISMATCH
    ) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

        Row(modifier = Modifier.padding(8.dp)) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Register as Admin")
        }

        if(isAdmin) {
            TextField(
                label = { Text("Company Name") },
                value = companyName,
                onValueChange = { companyName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp ,Color.Transparent)
            )
        }

            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if(hasEmailError) Color.Red else Color.Transparent)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordTextField(
                password = password,
                onValueChange = { password = it },
                label = "Password",
                modifier = Modifier
                    .border(1.dp, if(hasPasswordError) Color.Red else Color.Transparent),
                authViewModel
            )

            PasswordTextField(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                modifier = Modifier
                    .border(1.dp, if(hasConfirmPasswordError) Color.Red else Color.Transparent),
                authViewModel
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(isAdmin) {
                        userViewModel.isAdmin()
                        authViewModel.registerAndCreateUserInFirestore(
                            email,
                            password,
                            confirmPassword,
                            companyName
                        ) } else {
                        authViewModel.registerAndCreateUserInFirestore(
                            email,
                            password,
                            confirmPassword,
                            companyName)
                    } },
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
    RegisterScreen(authViewModel = viewModel(), goLogin = {}, userViewModel = viewModel())
}