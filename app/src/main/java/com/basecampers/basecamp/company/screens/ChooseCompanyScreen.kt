package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.company.models.CompanyModel
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
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

	// Filter companies
	val userCompanies = companies.filter { company ->
		userProfile?.companyList?.contains(company.companyId) == true
	}
	val availableCompanies = companies.filter { company ->
		userProfile?.companyList?.contains(company.companyId) != true
	}

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
					text = "Choose Company",
					style = MaterialTheme.typography.headlineMedium.copy(
						fontWeight = FontWeight.Bold
					),
					color = TextPrimary
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = "Select or join a company to continue",
					style = MaterialTheme.typography.bodyMedium,
					color = TextSecondary,
					textAlign = TextAlign.Center
				)
			}
		}

		// Main Content
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			// Your Companies Section
			item {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = Color.White
					),
					elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
					shape = RoundedCornerShape(16.dp)
				) {
					Column(modifier = Modifier.padding(16.dp)) {
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier.fillMaxWidth()
						) {
							Icon(
								imageVector = Icons.Default.Business,
								contentDescription = "Your Companies",
								tint = SecondaryAqua,
								modifier = Modifier.size(24.dp)
							)
							Spacer(modifier = Modifier.width(8.dp))
							Text(
								text = "Your Companies",
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.Bold
							)
						}

						Spacer(modifier = Modifier.height(16.dp))

						if (userCompanies.isNotEmpty()) {
							Column {
								userCompanies.forEach { company ->
									CompanyListItem(
										company = company,
										isSelected = company.companyId == UserSession.selectedCompanyId.value,
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
								Column(
									horizontalAlignment = Alignment.CenterHorizontally
								) {
									Icon(
										imageVector = Icons.Default.Business,
										contentDescription = "No Companies",
										tint = TextSecondary,
										modifier = Modifier.size(48.dp)
									)
									Spacer(modifier = Modifier.height(8.dp))
									Text(
										text = "No companies added yet",
										style = MaterialTheme.typography.bodyMedium,
										color = TextSecondary
									)
								}
							}
						}
					}
				}
			}

			// Available Companies Section
			if (availableCompanies.isNotEmpty()) {
				item {
					Card(
						modifier = Modifier.fillMaxWidth(),
						colors = CardDefaults.cardColors(
							containerColor = Color.White
						),
						elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
						shape = RoundedCornerShape(16.dp)
					) {
						Column(modifier = Modifier.padding(16.dp)) {
							Row(
								verticalAlignment = Alignment.CenterVertically,
								modifier = Modifier.fillMaxWidth()
							) {
								Icon(
									imageVector = Icons.Default.AddBusiness,
									contentDescription = "Available Companies",
									tint = SecondaryAqua,
									modifier = Modifier.size(24.dp)
								)
								Spacer(modifier = Modifier.width(8.dp))
								Text(
									text = "Available Companies",
									style = MaterialTheme.typography.titleMedium,
									fontWeight = FontWeight.Bold
								)
							}

							Spacer(modifier = Modifier.height(16.dp))

							OutlinedTextField(
								value = selectedCompany?.companyName ?: "Select a company",
								onValueChange = {},
								modifier = Modifier
									.fillMaxWidth()
									.clickable { expanded = true },
								enabled = false,
								readOnly = true,
								trailingIcon = {
									Icon(
										imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
										contentDescription = null,
										tint = SecondaryAqua
									)
								},
								colors = TextFieldDefaults.outlinedTextFieldColors(
									disabledTextColor = TextPrimary,
									disabledBorderColor = SecondaryAqua.copy(alpha = 0.5f),
									disabledLabelColor = TextSecondary
								)
							)

							DropdownMenu(
								expanded = expanded,
								onDismissRequest = { expanded = false },
								modifier = Modifier.fillMaxWidth()
							) {
								availableCompanies.forEach { company ->
									DropdownMenuItem(
										text = { Text(company.companyName) },
										onClick = {
											selectedCompany = company
											expanded = false
										}
									)
								}
							}

							Spacer(modifier = Modifier.height(8.dp))

							Button(
								onClick = {
									selectedCompany?.let {
										companyViewModel.joinCompany(it.companyId, userId)
									}
								},
								enabled = selectedCompany != null,
								modifier = Modifier.fillMaxWidth(),
								colors = ButtonDefaults.buttonColors(
									containerColor = SecondaryAqua
								),
								shape = RoundedCornerShape(12.dp)
							) {
								Icon(
									imageVector = Icons.Default.PersonAdd,
									contentDescription = "Join",
									modifier = Modifier.size(20.dp)
								)
								Spacer(modifier = Modifier.width(8.dp))
								Text("Join Company")
							}
						}
					}
				}
			}

			// Create Company Button
			item {
				Button(
					onClick = goCreateCompany,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(
						containerColor = SecondaryAqua
					),
					shape = RoundedCornerShape(12.dp)
				) {
					Icon(
						imageVector = Icons.Default.Add,
						contentDescription = "Create Company",
						modifier = Modifier.size(20.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text("Create New Company")
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
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onCompanySelected() },
		colors = CardDefaults.cardColors(
			containerColor = if (isSelected) SecondaryAqua.copy(alpha = 0.1f) else Color.White
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = RoundedCornerShape(12.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Company Icon
			Box(
				modifier = Modifier
					.size(40.dp)
					.clip(CircleShape)
					.background(SecondaryAqua.copy(alpha = 0.1f)),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = Icons.Default.Business,
					contentDescription = null,
					tint = SecondaryAqua,
					modifier = Modifier.size(20.dp)
				)
			}

			Spacer(modifier = Modifier.width(16.dp))

			// Company Info
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = company.companyName,
					style = MaterialTheme.typography.bodyMedium,
					color = TextPrimary
				)
				Text(
					text = company.companyId,
					style = MaterialTheme.typography.bodySmall,
					color = TextSecondary
				)
			}

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
}