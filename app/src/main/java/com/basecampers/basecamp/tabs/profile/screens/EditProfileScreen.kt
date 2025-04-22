package com.basecampers.basecamp.tabs.profile.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@Composable
fun EditProfileScreen(
	profileViewModel: ProfileViewModel = viewModel(),
	onNavigateBack: () -> Unit = {}
) {
	val profile by UserSession.profile.collectAsState()
	var firstName by remember { mutableStateOf(profile?.firstName ?: "") }
	var lastName by remember { mutableStateOf(profile?.lastName ?: "") }
	val isLoading by profileViewModel.isLoading.collectAsState()

	// Image picker
	var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
	val imagePickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.GetContent()
	) { uri: Uri? ->
		uri?.let {
			selectedImageUri = it
			profileViewModel.uploadProfilePicture(it)
		}
	}

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

		// Profile Picture
		// Profile Picture
		Box(
			modifier = Modifier
				.size(120.dp)
				.clickable { imagePickerLauncher.launch("image/*") },
			contentAlignment = Alignment.Center
		) {
			val imagePainter = rememberAsyncImagePainter(
				model = selectedImageUri ?: profile?.profilePictureUrl
			)

			if ((selectedImageUri == null) && profile?.profilePictureUrl.isNullOrBlank()) {
				// Placeholder with border and text
				Box(
					modifier = Modifier
						.fillMaxSize()
						.border(2.dp, MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.medium)
						.padding(8.dp),
					contentAlignment = Alignment.Center
				) {
					Text(
						text = "Choose\nProfile Pic",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.primary,
						textAlign = androidx.compose.ui.text.style.TextAlign.Center
					)
				}
			} else {
				// Display image
				Image(
					painter = imagePainter,
					contentDescription = "Profile Picture",
					modifier = Modifier.fillMaxSize(),
					contentScale = ContentScale.Crop
				)
			}
		}

		Spacer(modifier = Modifier.height(24.dp))

		OutlinedTextField(
			value = firstName,
			onValueChange = { firstName = it },
			label = { Text("First Name") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		)

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
					onSuccess = { onNavigateBack() }
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
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 8.dp)
		) {
			Text("Cancel")
		}
	}
}
