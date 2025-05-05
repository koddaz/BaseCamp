package com.basecampers.basecamp.authentication

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.*
import com.basecampers.basecamp.ui.theme.*

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(goRegister = {}, goForgotPass = {}, authViewModel = viewModel())
}

@Composable
fun LoginScreen(
    goRegister: () -> Unit,
    goForgotPass: () -> Unit,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showTestButtons by remember { mutableStateOf(false) }
    val isLoggedIn by authViewModel.loggedin.collectAsState()
    val loginErrorMessage by authViewModel.loginErrorMessage.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Log.d("LoginScreen", "User logged in successfully")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    if(loginErrorMessage.isNotEmpty()) {
                        authViewModel.clearLoginErrors()
                    }
                },
                label = { Text("Email", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    if(loginErrorMessage.isNotEmpty()) {
                        authViewModel.clearLoginErrors()
                    }
                },
                label = { Text("Password", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
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

            // Error Message
            if (loginErrorMessage.isNotEmpty()) {
                Text(
                    text = "Incorrect email or password",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    isLoading = true
                    authViewModel.login(
                        email = email,
                        password = password,
                        onSuccess = { isLoading = false },
                        onError = { isLoading = false }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryAqua,
                    disabledContainerColor = SecondaryAqua.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password Button
            TextButton(
                onClick = goForgotPass,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    "Forgot Password?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryAqua
                )
            }
            /*
            // Test Accounts Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTestButtons = !showTestButtons }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Test Accounts",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryAqua,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle test accounts",
                    tint = SecondaryAqua
                )
            }

            // Test Buttons
            AnimatedVisibility(
                visible = showTestButtons,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TestButton("User 1 (Admin)") {
                        authViewModel.loginUser1()
                    }
                    TestButton("User 2 (SuperUser)") {
                        authViewModel.loginUser2()
                    }
                    TestButton("User 3 (User)") {
                        authViewModel.loginUser3()
                    }
                }
            }

             */

            Spacer(modifier = Modifier.weight(1f))
            
            // Register Button
            TextButton(
                onClick = goRegister,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryAqua
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}