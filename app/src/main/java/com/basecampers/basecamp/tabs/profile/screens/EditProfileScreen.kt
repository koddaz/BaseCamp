package com.basecampers.basecamp.tabs.profile.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@Composable
fun EditProfileScreen(
	profileViewModel: ProfileViewModel = viewModel(),
	onNavigateBack: () -> Unit = {}
) {
	
	// Get current profile data from UserSession
	val profile by UserSession.profile.collectAsState()
	
	// Local state for text fields
	var firstName by remember { mutableStateOf(profile?.firstName ?: "") }
	var lastName by remember { mutableStateOf(profile?.lastName ?: "") }
	
	// Loading state
	val isLoading by profileViewModel.isLoading.collectAsState()
	
	// Update text fields when profile changes
	LaunchedEffect(profile) {
		firstName = profile?.firstName ?: ""
		lastName = profile?.lastName ?: ""
	}
	
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = "Edit Profile",
			style = MaterialTheme.typography.headlineSmall,
			modifier = Modifier.padding(bottom = 24.dp)
		)
		
		// First Name Field
		OutlinedTextField(
			value = firstName,
			onValueChange = { firstName = it },
			label = { Text("First Name") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		)
		
		// Last Name Field
		OutlinedTextField(
			value = lastName,
			onValueChange = { lastName = it },
			label = { Text("Last Name") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 24.dp)
		)
		
		Button(
			onClick = {
				profileViewModel.updateUserName(
					firstName = firstName,
					lastName = lastName,
					onSuccess = {
						onNavigateBack()
					}
				)
			},
			modifier = Modifier.fillMaxWidth(),
			enabled = !isLoading
		) {
			if (isLoading) {
				CircularProgressIndicator(
					modifier = Modifier.size(24.dp),
					color = MaterialTheme.colorScheme.onPrimary,
					strokeWidth = 2.dp
				)
			} else {
				Text("Update Profile")
			}
		}
		
		TextButton(
			onClick = { onNavigateBack() },
			modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
		) {
			Text("Cancel")
		}
	}
}