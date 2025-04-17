package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.basecampers.basecamp.company.CompanyViewModel
import com.basecampers.basecamp.tabs.profile.CompanyListItem
import com.basecampers.basecamp.tabs.profile.models.CompanyModel
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

	val userCompanies = companies.filter { company ->
		userProfile?.companyList?.contains(company.companyId) == true
	}

	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.SpaceBetween
	) {
		Text(
			text = "Choose Company",
			fontSize = 24.sp,
			textAlign = TextAlign.Center,
			modifier = Modifier.padding(top = 32.dp)
		)

		Card(
			modifier = Modifier
				.fillMaxWidth()
				.weight(1f)
				.padding(vertical = 16.dp),
			elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				Text(
					text = "Your Companies",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)

				Spacer(modifier = Modifier.height(8.dp))

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
							color = Color.Gray
						)
					}
				}
			}
		}
		
		Button(
			onClick = goCreateCompany,
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		) {
			Text("Create Company")
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		) {
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
						imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
						contentDescription = null
					)
				}
			)

			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
				modifier = Modifier.fillMaxWidth()
			) {
				companies.forEach { company ->
					DropdownMenuItem(
						text = { Text(company.companyName) },
						onClick = {
							selectedCompany = company
							expanded = false
						}
					)
				}
			}
		}

		// Join button (visible only when a company is selected)
		Button(
			onClick = {
				selectedCompany?.let {
					companyViewModel.joinCompany(it.companyId, userId)
				}
			},
			enabled = selectedCompany != null,
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		) {
			Text("Join")
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
		// Company Icon placeholder
		Box(
			modifier = Modifier
				.size(32.dp)
				.background(Color.LightGray, CircleShape),
			contentAlignment = Alignment.Center
		) {
			Icon(
				imageVector = Icons.Default.Business,
				contentDescription = null,
				tint = Color.White,
				modifier = Modifier.size(16.dp)
			)
		}

		Spacer(modifier = Modifier.width(8.dp))

		// Company Name
		Text(
			text = company.companyName,
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.weight(1f)
		)

		// Selected indicator
		if (isSelected) {
			Icon(
				imageVector = Icons.Default.Check,
				contentDescription = "Selected",
				tint = MaterialTheme.colorScheme.primary
			)
		}
	}
}