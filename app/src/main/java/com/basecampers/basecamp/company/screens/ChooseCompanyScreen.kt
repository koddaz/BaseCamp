package com.basecampers.basecamp.company.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.aRootFolder.UserSession.selectedCompanyId
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.company.models.CompanyModel
import com.basecampers.basecamp.ui.theme.*
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun ChooseCompanyScreen(
	companyViewModel: CompanyViewModel,
	authViewModel: AuthViewModel,
	goCreateCompany: () -> Unit
) {
	val companies by companyViewModel.companies.collectAsState()
	val userProfile by UserSession.profile.collectAsState()
	var selectedCompany by remember { mutableStateOf<CompanyModel?>(null) }
	var expanded by remember { mutableStateOf(false) }
	val userId = Firebase.auth.currentUser?.uid ?: ""

	// Filter companies, show only those that the user is a member of
	val userCompanies = companies.filter { company ->
		userProfile?.companyList?.contains(company.companyId) == true
	}
	// Filter companies, show only those that the user is not a member of
	val availableCompanies = companies.filter { company ->
		userProfile?.companyList?.contains(company.companyId) != true
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
					fontWeight = FontWeight.Bold
				),
				color = TextPrimary
			)

			Spacer(modifier = Modifier.height(32.dp))

			// Your Companies Section
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp),
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(
					containerColor = Color.White
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(modifier = Modifier.padding(16.dp)) {
					Text(
						text = "Your Companies",
						style = MaterialTheme.typography.titleMedium,
						fontWeight = FontWeight.Bold,
						color = TextPrimary
					)

					Spacer(modifier = Modifier.height(16.dp))

					if (userCompanies.isNotEmpty()) {
						Column {
							userCompanies.forEach { company ->
								CompanyListItem(
									company = company,
									isSelected = company.companyName == selectedCompanyId.value,
									onCompanySelected = {
										companyViewModel.selectCompany(company.companyId, userId)
									}
								)
							}
						}
					} else {
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 16.dp),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = "No companies added yet",
								style = MaterialTheme.typography.bodyMedium,
								color = TextSecondary
							)
						}
					}
				}
			}

			// Available Companies Dropdown
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 16.dp)
			) {
				OutlinedTextField(
					value = selectedCompany?.companyName ?: "Select a company to join",
					onValueChange = {},
					modifier = Modifier
						.fillMaxWidth()
						.clickable { expanded = true },
					enabled = false,
					readOnly = true,
					shape = RoundedCornerShape(12.dp),
					colors = OutlinedTextFieldDefaults.colors(
						focusedBorderColor = SecondaryAqua,
						unfocusedBorderColor = Color.Gray
					),
					trailingIcon = {
						Icon(
							imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
							contentDescription = "Toggle company list",
							tint = SecondaryAqua
						)
					}
				)

				DropdownMenu(
					expanded = expanded,
					onDismissRequest = { expanded = false },
					modifier = Modifier
						.fillMaxWidth()
						.background(Color.White, RoundedCornerShape(12.dp))
				) {
					availableCompanies.forEach { company ->
						DropdownMenuItem(
							text = { 
								Text(
									text = company.companyName,
									color = TextPrimary
								)
							},
							onClick = {
								selectedCompany = company
								expanded = false
							}
						)
					}
				}
			}

			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 8.dp)
			) {
				// Join Button
				Button(
					onClick = {
						selectedCompany?.let {
							companyViewModel.joinCompany(it.companyId, userId)
						}
					},
					enabled = selectedCompany != null,
					modifier = Modifier
						.fillMaxWidth()
						.height(56.dp)
						.padding(bottom = 16.dp),
					shape = RoundedCornerShape(12.dp)
				) {
					Text("Join Company", fontSize = 16.sp)
				}

				// Create Company Button
				OutlinedButton(
					onClick = goCreateCompany,
					modifier = Modifier
						.fillMaxWidth()
						.height(74.dp)
						.padding(bottom = 32.dp),
					shape = RoundedCornerShape(12.dp),
					border = BorderStroke(1.dp, SecondaryAqua),
					colors = ButtonDefaults.outlinedButtonColors(
						contentColor = SecondaryAqua
					)
				) {
					Text(
						text = "Create New Company",
						fontSize = 16.sp,
						maxLines = 1
					)
				}
			}
		}
	}
}

@Composable
fun CompanyListItem(
	company: CompanyModel,
	isSelected: Boolean,
	onCompanySelected: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onCompanySelected() }
			.padding(vertical = 8.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		// Company Icon
		Box(
			modifier = Modifier
				.size(40.dp)
				.background(SecondaryAqua.copy(alpha = 0.1f), CircleShape),
			contentAlignment = Alignment.Center
		) {
			Icon(
				imageVector = Icons.Default.Business,
				contentDescription = "Company icon",
				tint = SecondaryAqua,
				modifier = Modifier.size(20.dp)
			)
		}

		Spacer(modifier = Modifier.width(16.dp))

		// Company Name
		Text(
			text = company.companyName,
			style = MaterialTheme.typography.bodyLarge,
			color = TextPrimary,
			modifier = Modifier.weight(1f)
		)

		// Selected indicator
		if (isSelected) {
			Icon(
				imageVector = Icons.Default.Check,
				contentDescription = "Selected",
				tint = SecondaryAqua
			)
		}
	}
}