
package com.basecampers.basecamp.authentication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel

@Composable
fun ForgotPasswordScreen(authViewModel : AuthViewModel) {

    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Ska ändras till forgot password
        Text("Forgot password", style = MaterialTheme.typography.headlineMedium)

        TextField(
            label = { Text("Email") },
            value = email,
            onValueChange = {email = it},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            authViewModel.forgotPassword(email)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Send Email")
        }
    }
}