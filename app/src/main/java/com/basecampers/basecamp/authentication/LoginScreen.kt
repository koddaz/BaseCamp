package com.basecampers.basecamp.authentication

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.*
import com.basecampers.basecamp.ui.theme.*
import com.basecampers.basecamp.BuildConfig

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
    val isLoggedIn by authViewModel.loggedin.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            Log.d("LoginScreen", "User logged in successfully")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp)
    ) {
        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.TopCenter)
        ) {
            Text(
                text = "Pattern Placeholder",
                color = SecondaryAqua,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

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
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            PasswordTextFieldLogin(
                password = password,
                onValueChange = {
                    password = it
                    if(loginErrorMessage.isNotEmpty()) {
                        authViewModel.clearLoginErrors()
                    } },
                label = "Password",
                authViewModel = authViewModel,
                modifier = Modifier
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

            Button(onClick = {
                authViewModel.login(email = "admin@admin.se", password = "Test123!")
            }) {
                Text("admin@admin.se & Test123!")
            }

            Button(onClick = {
                authViewModel.login(email = "user9182@example.com", password = "Test123!")
            }) {
                Text("user9182@example.com & Test123!")
            }
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
                Text("User 1 (User)")
            }

            Button(onClick = {
                authViewModel.loginUser2()
            }) {
                Text("User 2 (SuperUser)")
            }

            Button(onClick = {
                authViewModel.loginUser3()
            }) {
                Text("User 3 (Admin)")
            }

            Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { goRegister() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(goRegister = {}, goForgotPass = {}, authViewModel = viewModel())
}