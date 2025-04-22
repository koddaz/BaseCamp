package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCompanyScreen(
	companyViewModel: CompanyViewModel,
	goChooseCompany: () -> Unit
) {
	var companyName by remember { mutableStateOf("") }
	var errorMessage by remember { mutableStateOf("") }
	var isLoading by remember { mutableStateOf(false) }

	Column(
		modifier = Modifier
			.fillMaxSize()
			.background(AppBackground)
	) {
		// Header with Gradient Background
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(160.dp)
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
				Text(
					text = "Create Company",
					style = MaterialTheme.typography.headlineMedium.copy(
						fontWeight = FontWeight.Bold
					),
					color = TextPrimary
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "Start your company journey",
					style = MaterialTheme.typography.bodyMedium,
					color = TextSecondary,
					textAlign = TextAlign.Center
				)
			}
		}

		// Main Content
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			colors = CardDefaults.cardColors(
				containerColor = Color.White
			),
			elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
			shape = RoundedCornerShape(16.dp)
		) {
			Column(
				modifier = Modifier.padding(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				// Company Name Input
				OutlinedTextField(
					value = companyName,
					onValueChange = { companyName = it },
					label = { Text("Company Name") },
					modifier = Modifier.fillMaxWidth(),
					singleLine = true,
					isError = errorMessage.isNotEmpty(),
					colors = TextFieldDefaults.outlinedTextFieldColors(
						focusedBorderColor = SecondaryAqua,
						unfocusedBorderColor = SecondaryAqua.copy(alpha = 0.5f),
						focusedLabelColor = SecondaryAqua,
						unfocusedLabelColor = TextSecondary
					),
					leadingIcon = {
						Icon(
							imageVector = Icons.Default.Business,
							contentDescription = "Company Name",
							tint = SecondaryAqua
						)
					}
				)

				if (errorMessage.isNotEmpty()) {
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						text = errorMessage,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.error
					)
				}

				Spacer(modifier = Modifier.height(24.dp))

				// Create Button
				Button(
					onClick = {
						if (companyName.isBlank()) {
							errorMessage = "Company name cannot be empty"
							return@Button
						}

						val userId = Firebase.auth.currentUser?.uid
						if (userId == null) {
							errorMessage = "User not authenticated"
							return@Button
						}

						isLoading = true
						errorMessage = ""

						companyViewModel.createCompany(
							companyName = companyName,
							userId = userId,
							onSuccess = {
								isLoading = false
								goChooseCompany()
							},
							onError = { error ->
								isLoading = false
								errorMessage = error
							}
						)
					},
					modifier = Modifier.fillMaxWidth(),
					enabled = !isLoading,
					colors = ButtonDefaults.buttonColors(
						containerColor = SecondaryAqua
					),
					shape = RoundedCornerShape(12.dp)
				) {
					if (isLoading) {
						CircularProgressIndicator(
							modifier = Modifier.size(24.dp),
							color = Color.White,
							strokeWidth = 2.dp
						)
					} else {
						Icon(
							imageVector = Icons.Default.Add,
							contentDescription = "Create",
							modifier = Modifier.size(20.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text("Create Company")
					}
				}

				Spacer(modifier = Modifier.height(16.dp))

				// Back Button
				OutlinedButton(
					onClick = goChooseCompany,
					modifier = Modifier.fillMaxWidth(),
					enabled = !isLoading,
					colors = ButtonDefaults.outlinedButtonColors(
						contentColor = SecondaryAqua
					),
					shape = RoundedCornerShape(12.dp)
				) {
					Icon(
						imageVector = Icons.Default.ArrowBack,
						contentDescription = "Back",
						modifier = Modifier.size(20.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text("Back to Choose")
				}
			}
		}
	}
}