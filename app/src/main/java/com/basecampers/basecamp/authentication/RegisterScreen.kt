
package com.basecampers.basecamp.authentication

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.ConfirmPasswordTextField
import com.basecampers.basecamp.components.PasswordInfoButton
import com.basecampers.basecamp.components.PasswordPolicyInfo
import com.basecampers.basecamp.components.PasswordTextField

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
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    .border(1.dp , Color.Transparent)
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
                        isEmailValid -> Color.Green
                        hasEmailError && email.isNotEmpty() -> Color.Red
                        else -> Color.LightGray
                    }
                )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            PasswordTextField(
                password = password,
                onValueChange = { password = it },
                label = "Password",
                authViewModel = authViewModel,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(8.dp))
            
            PasswordInfoButton(onInfoClick = { showPasswordPolicy = !showPasswordPolicy })
        }
        PasswordPolicyInfo(visible = showPasswordPolicy)
        
        ConfirmPasswordTextField(
            password = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            authViewModel = authViewModel,
            modifier = Modifier
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                authViewModel.registerAndCreateUserInFirestore(
                    email, password,
                    confirmPassword = confirmPassword,
                    companyName = companyName
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.weight(1f))
        
        Button(onClick = {
            goLogin()
        }) {
            Text("Go to Login")
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(authViewModel = viewModel(), goLogin = {})
}
