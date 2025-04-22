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
import com.basecampers.basecamp.components.*
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel, goLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var showPasswordPolicy by remember { mutableStateOf(false) }
    val hasEmailError by authViewModel.hasEmailError.collectAsState()
    val errorMessage by authViewModel.registerErrorMessage.collectAsState()
    val isEmailValid by authViewModel.emailValid.collectAsState()

    val emailErrors = errorMessage.filter { error ->
        error in listOf(
            AuthViewModel.RegisterErrors.EMAIL_ALREADY_IN_USE,
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PasswordPolicyInfo(
                visible = showPasswordPolicy,
                onDismiss = { showPasswordPolicy = false },
                modifier = Modifier.zIndex(10f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Logo/App Name
                Text(
                    text = "Basecamp",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email Field
                BasecampTextField(
                    value = email,
                    onValueChange = {
                        if (hasEmailError && it != email) {
                            authViewModel.clearEmailErrors()
                        }
                        email = it
                        authViewModel.validateEmailLive(it)
                    },
                    label = "Email",
                    isError = hasEmailError,
                    modifier = Modifier.fillMaxWidth()
                )

                // Display email error messages
                emailErrors.forEach { error ->
                    Text(
                        text = when (error) {
                            AuthViewModel.RegisterErrors.EMAIL_ALREADY_IN_USE -> "Email is already in use"
                            AuthViewModel.RegisterErrors.EMAIL_EMPTY -> "Email cannot be empty"
                            AuthViewModel.RegisterErrors.EMAIL_NOT_VALID -> "Please enter a valid email"
                            else -> "Unknown error"
                        },
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field with Policy Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasecampTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        isError = passwordError.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PasswordInfoButton(
                        onInfoClick = { showPasswordPolicy = true }
                    )
                }

                // Display password error messages
                passwordError.forEach { error ->
                    Text(
                        text = when (error) {
                            AuthViewModel.RegisterErrors.PASSWORD_EMPTY -> "Password cannot be empty"
                            AuthViewModel.RegisterErrors.PASSWORD_TOO_SHORT -> "Password must be at least 8 characters"
                            AuthViewModel.RegisterErrors.PASSWORD_NO_SPECIAL_CHAR -> "Password must contain at least one special character"
                            AuthViewModel.RegisterErrors.PASSWORD_NO_UPPERCASE -> "Password must contain at least one uppercase letter"
                            AuthViewModel.RegisterErrors.PASSWORD_NO_NUMBER -> "Password must contain at least one number"
                            else -> "Unknown error"
                        },
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password Field
                BasecampTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    isError = confirmPasswordError.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                                tint = TextSecondary
                            )
                        }
                    }
                )

                // Display confirm password error messages
                confirmPasswordError.forEach { error ->
                    Text(
                        text = when (error) {
                            AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_EMPTY -> "Please confirm your password"
                            AuthViewModel.RegisterErrors.CONFIRM_PASSWORD_MISMATCH -> "Passwords do not match"
                            else -> "Unknown error"
                        },
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Register button
                BasecampButton(
                    text = "Register",
                    onClick = {
                        authViewModel.registerAsUser(
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            profileViewModel = profileViewModel,
                            onSuccess = { /* Handle success */ },
                            onError = { /* Handle error */ }
                        )
                    },
                    enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
                    color = SecondaryAqua
                )

                Spacer(modifier = Modifier.weight(1f))

                // Go to login button
                BasecampOutlinedButton(
                    text = "Already have an account? Login",
                    onClick = goLogin
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(authViewModel = viewModel(), profileViewModel = viewModel(), goLogin = {})
}
