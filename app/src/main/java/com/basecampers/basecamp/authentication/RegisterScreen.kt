
package com.basecampers.basecamp.authentication

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.ConfirmPasswordTextField
import com.basecampers.basecamp.components.PasswordInfoButton
import com.basecampers.basecamp.components.PasswordPolicyInfo
import com.basecampers.basecamp.components.PasswordTextField
import java.util.UUID

@Composable
fun RegisterScreen(authViewModel : AuthViewModel, goLogin : () -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswordPolicy by remember { mutableStateOf(false) }
    val hasEmailError by authViewModel.hasEmailError.collectAsState()
    val errorMessage by authViewModel.registerErrorMessage.collectAsState()
    val isEmailValid by authViewModel.emailValid.collectAsState()
    //Maybe remove?
    var isAdmin by remember { mutableStateOf(false) }
    var companyName by remember { mutableStateOf("") }

    val emailErrors = errorMessage.filter { error ->
        error in listOf(
            AuthViewModel.RegisterErrors.EMAIL_EMPTY,
            AuthViewModel.RegisterErrors.EMAIL_NOT_VALID
        )
    }
    val passwordError = errorMessage.filter { error ->
        error in listOf(
            AuthViewModel.RegisterErrors.PASSWORD_EMPTY,
            AuthViewModel.RegisterErrors.PASSWORD_TOO_SHORT,
            AuthViewModel.RegisterErrors.PASSWORD_NO_SPECIAL_CHAR,
            AuthViewModel.RegisterErrors.PASSWORD_NO_UPPERCASE,
            AuthViewModel.RegisterErrors.PASSWORD_NO_NUMBER
        )
    }
    val confirmPasswordError = errorMessage.filter { error ->
        error in listOf(
            AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_EMPTY,
            AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_MISMATCH
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PasswordPolicyInfo(
            visible = showPasswordPolicy,
            onDismiss = { showPasswordPolicy = false },
            modifier = Modifier.zIndex(10f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Register", style = MaterialTheme.typography.headlineMedium)

            // Admin checkbox and related fields
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
                Text("Register as Admin")
            }

            if (isAdmin) {
                TextField(
                    label = { Text("Company Name") },
                    value = companyName,
                    onValueChange = { companyName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Email field
            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = {
                    // Same email logic
                    email = it
                    authViewModel.validateEmailLive(it)
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Password fields
            Row(verticalAlignment = Alignment.CenterVertically) {
                PasswordTextField(
                    password = password,
                    onValueChange = { password = it },
                    label = "Password",
                    authViewModel = authViewModel,
                    modifier = Modifier.weight(1f)
                )
                PasswordInfoButton(onInfoClick = { showPasswordPolicy = !showPasswordPolicy })
            }

            ConfirmPasswordTextField(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                authViewModel = authViewModel,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Button(
                onClick = { /* Random company logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Random Company")
            }

            Button(
                onClick = { /* Random user logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Random User")
            }

            Button(
                onClick = {
                    authViewModel.registerAsUser(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { goLogin() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Login")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(authViewModel = viewModel(), goLogin = {})
}
