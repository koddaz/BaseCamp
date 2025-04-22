package com.basecampers.basecamp.tabs.profile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.ui.theme.*

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
			.background(AppBackground)
			.verticalScroll(rememberScrollState())
	) {
		// Header with Gradient Background
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(180.dp)
				.background(
					brush = Brush.verticalGradient(
						colors = listOf(
							SecondaryAqua.copy(alpha = 0.2f),
							AppBackground
						)
					)
				)
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				// Back Button
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 16.dp),
					horizontalArrangement = Arrangement.Start
				) {
					IconButton(
						onClick = onNavigateBack,
						modifier = Modifier
							.size(48.dp)
							.clip(CircleShape)
							.background(SecondaryAqua.copy(alpha = 0.1f))
					) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back",
							tint = SecondaryAqua
						)
					}
				}

				// Profile Image with Border
				Box(
					modifier = Modifier
						.size(80.dp)
						.clip(CircleShape)
						.background(SecondaryAqua.copy(alpha = 0.1f))
						.padding(4.dp),
					contentAlignment = Alignment.Center
				) {
					Icon(
						imageVector = Icons.Default.Person,
						contentDescription = "Profile Picture",
						tint = SecondaryAqua,
						modifier = Modifier.size(40.dp)
					)
				}
			}
		}

		// Main Content
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = "Edit Profile",
				style = MaterialTheme.typography.headlineMedium.copy(
					fontWeight = FontWeight.Bold
				),
				color = TextPrimary,
				modifier = Modifier.padding(bottom = 24.dp)
			)

			// Input Fields Card
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = Color.White
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
				shape = RoundedCornerShape(16.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					// First Name Field
					OutlinedTextField(
						value = firstName,
						onValueChange = { firstName = it },
						label = { Text("First Name") },
						modifier = Modifier.fillMaxWidth(),
						colors = OutlinedTextFieldDefaults.colors(
							focusedBorderColor = SecondaryAqua,
							focusedLabelColor = SecondaryAqua
						),
						shape = RoundedCornerShape(12.dp)
					)

					// Last Name Field
					OutlinedTextField(
						value = lastName,
						onValueChange = { lastName = it },
						label = { Text("Last Name") },
						modifier = Modifier.fillMaxWidth(),
						colors = OutlinedTextFieldDefaults.colors(
							focusedBorderColor = SecondaryAqua,
							focusedLabelColor = SecondaryAqua
						),
						shape = RoundedCornerShape(12.dp)
					)
				}
			}

			Spacer(modifier = Modifier.height(24.dp))

			// Update Button
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
				modifier = Modifier
					.fillMaxWidth()
					.height(56.dp),
				enabled = !isLoading && (firstName != profile?.firstName || lastName != profile?.lastName),
				colors = ButtonDefaults.buttonColors(
					containerColor = SecondaryAqua
				),
				shape = RoundedCornerShape(12.dp)
			) {
				if (isLoading) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp),
						color = Color.White
					)
				} else {
					Text(
						text = "Update Profile",
						style = MaterialTheme.typography.titleMedium.copy(
							fontWeight = FontWeight.Bold
						)
					)
				}
			}

			Spacer(modifier = Modifier.height(16.dp))

			// Cancel Button
			TextButton(
				onClick = onNavigateBack,
				modifier = Modifier.fillMaxWidth()
			) {
				Text(
					text = "Cancel",
					style = MaterialTheme.typography.titleMedium,
					color = TextSecondary
				)
			}
		}
	}
}