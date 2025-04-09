
package com.basecampers.basecamp.tabs.social

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SocialScreen() {
    Text("Social Screen", style = MaterialTheme.typography.titleLarge)
}
/* Profile screen var i SocialScreen.kt (sparar om den andra är borta)
package com.basecampers.basecamp.tabs.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.navigation.models.AuthViewModel

@Composable
fun ProfileScreen(authViewModel: AuthViewModel) {

    val userInfo by authViewModel.userInfo.collectAsState() // Now observing Firestore data

    val (username, userEmail) = userInfo
    val uid = authViewModel.getCurrentUserUid() // Get UID from Firebase Auth
    
    var showDeleteConfirmation by remember { mutableStateOf(false) } // Warning before delete

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text("You are logged in!", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Username: ${username ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        Text("Email: ${userEmail ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
        Text("UID: ${uid ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showDeleteConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Account")
        }
    }
    
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.deleteUser()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    
}

@Preview(showBackground = true)
@Composable
fun ProfilePreviewScreen() {
    ProfileScreen(authViewModel = viewModel())
}
*/
