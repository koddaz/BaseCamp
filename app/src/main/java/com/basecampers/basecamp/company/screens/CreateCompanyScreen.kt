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
import com.basecampers.basecamp.company.CompanyViewModel

@Composable
fun CreateCompanyScreen(
	companyViewModel: CompanyViewModel,
	goChooseCompany: () -> Unit
) {
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
		
		Spacer(modifier = Modifier.weight(1f))
		
		Button(
			onClick = {
				// This would normally create a company in Firebase
				// For now we'll just navigate back
				goChooseCompany()
			},
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp)
		) {
			Text("Create")
		}
		
		Button(
			onClick = goChooseCompany,
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 32.dp)
		) {
			Text("Back to Choose")
		}
	}
}