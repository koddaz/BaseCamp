package com.basecampers.basecamp.authentication

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.ConfirmPasswordTextField
import com.basecampers.basecamp.components.PasswordInfoButton
import com.basecampers.basecamp.components.PasswordPolicyInfo
import com.basecampers.basecamp.components.PasswordTextField
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel, goLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showPasswordPolicy by remember { mutableStateOf(false) }

    // Observe auth state
    val registerErrors by authViewModel.registerErrorMessage.collectAsState()
    val emailValid by authViewModel.emailValid.collectAsState()
    val passwordValid by authViewModel.passwordValid.collectAsState()
    val confirmPasswordValid by authViewModel.confirmPasswordValid.collectAsState()
    val hasEmailError by authViewModel.hasEmailError.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        PasswordPolicyInfo(
            visible = showPasswordPolicy,
            onDismiss = { showPasswordPolicy = false },
            modifier = Modifier.zIndex(10f)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 180.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 24.dp)
            )

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

            // Display email error messages
            registerErrors.filter { error ->
                error in listOf(
                    AuthViewModel.RegisterErrors.EMAIL_ALREADY_IN_USE,
                    AuthViewModel.RegisterErrors.EMAIL_EMPTY,
                    AuthViewModel.RegisterErrors.EMAIL_NOT_VALID
                )
            }.forEach { error ->
                Text(
                    text = error.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            TextField(
                label = { Text("Email") },
                value = email,
                onValueChange = {
                    if (hasEmailError && it != email) {
                        authViewModel.clearEmailErrors()
                    }
                    email = it
                    authViewModel.validateEmailLive(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        when {
                            hasEmailError -> Color.Red
                            emailValid && email.isNotEmpty() -> Color.Green
                            else -> Color.LightGray
                        }
                    )
            )

            // Password Field
            PasswordTextField(
                password = password,
                onValueChange = { password = it },
                label = "Password",
                modifier = Modifier.fillMaxWidth(),
                authViewModel = authViewModel
            )

            // Confirm Password Field
            ConfirmPasswordTextField(
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                modifier = Modifier.fillMaxWidth(),
                authViewModel = authViewModel
            )

            // Phone Field
            TextField(
                label = { Text("Phone") },
                value = phone,
                onValueChange = { phone = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            // Terms & Conditions Text
            Text(
                text = "By signing up, you are agree to our Terms & Conditions and Policies",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Sign Up Button
            Button(
                onClick = {
                    isLoading = true
                    authViewModel.registerAsUser(
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        profileViewModel = profileViewModel,
                        onSuccess = { isLoading = false },
                        onError = { isLoading = false }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Sign Up")
                }
            }

            // Login Link
            Text(
                text = "Already have an account? Login",
                color = TextSecondary,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable(onClick = goLogin)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        authViewModel = viewModel(),
        profileViewModel = viewModel(),
        goLogin = {}
    )
}
