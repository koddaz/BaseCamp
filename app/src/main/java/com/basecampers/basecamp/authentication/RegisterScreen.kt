package com.basecampers.basecamp.authentication

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Password Policy Info
        PasswordPolicyInfo(
            visible = showPasswordPolicy,
            onDismiss = { showPasswordPolicy = false },
            modifier = Modifier.zIndex(10f)
        )

        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(SecondaryAqua.copy(alpha = 0.1f))
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // Logo/App Name
            Text(
                text = "Basecamp",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Admin Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isAdmin,
                    onCheckedChange = { isAdmin = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SecondaryAqua,
                        uncheckedColor = Color.Gray
                    )
                )
                Text(
                    text = "Register as Admin",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Company Name Field (Admin only)
            AnimatedVisibility(
                visible = isAdmin,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Company Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryAqua,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    if (hasEmailError && it != email) {
                        authViewModel.clearEmailErrors()
                    }
                    email = it
                    authViewModel.validateEmailLive(it)
                },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (emailValid && email.isNotEmpty()) Color.Green else SecondaryAqua,
                    unfocusedBorderColor = if (hasEmailError) Color.Red else Color.Gray
                ),
                isError = hasEmailError
            )

            // Display email error messages
            if (hasEmailError) {
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
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Terms & Conditions Text
            Text(
                text = "By signing up, you agree to our Terms & Conditions and Policies",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.Start)
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
                    .height(56.dp),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Sign Up", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            TextButton(
                onClick = goLogin,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text("Already have an account? Sign In", color = SecondaryAqua)
            }
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
