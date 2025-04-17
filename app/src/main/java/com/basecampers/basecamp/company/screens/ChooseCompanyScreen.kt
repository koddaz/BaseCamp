package com.basecampers.basecamp.company.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyViewModel

@Composable
fun ChooseCompanyScreen(
	companyViewModel: CompanyViewModel,
	authViewModel: AuthViewModel,
	goCreateCompany: () -> Unit
) {
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
		
		Button(
			onClick = { companyViewModel.selectCompany("test-company-id", "current-user-id") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 32.dp)
		) {
			Text("Choose")
		}
		Button(
			onClick = { authViewModel.registerToTestCompany() },
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 32.dp)
		) {
			Text("Register to Test Company")
		}
	}
}