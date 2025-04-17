package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyViewModel
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
	var selectedCompany by remember { mutableStateOf<CompanyModel?>(null) }
	var expanded by remember { mutableStateOf(false) }
	val userId = Firebase.auth.currentUser?.uid ?: ""


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