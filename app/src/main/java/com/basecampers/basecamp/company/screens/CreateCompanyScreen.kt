package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.company.CompanyViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = "Create Company",
			fontSize = 24.sp,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(top = 32.dp)
		)

		Column(
			modifier = Modifier.weight(1f),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			OutlinedTextField(
				value = companyName,
				onValueChange = { companyName = it },
				label = { Text("Company Name") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp),
				singleLine = true,
				isError = errorMessage.isNotEmpty()
			)

			if (errorMessage.isNotEmpty()) {
				Text(
					text = errorMessage,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(top = 8.dp)
				)
			}
		}

		Column {
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
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				enabled = !isLoading
			) {
				if (isLoading) {
					CircularProgressIndicator(
						modifier = Modifier.size(24.dp),
						strokeWidth = 2.dp
					)
				} else {
					Text("Create")
				}
			}

			Button(
				onClick = goChooseCompany,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 32.dp),
				enabled = !isLoading
			) {
				Text("Back to Choose")
			}
		}
	}
}