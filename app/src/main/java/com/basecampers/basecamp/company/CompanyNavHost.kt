package com.basecampers.basecamp.company

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.models.CompanyRoutes
import com.basecampers.basecamp.company.screens.ChooseCompanyScreen
import com.basecampers.basecamp.company.screens.CreateCompanyScreen


@Composable
fun CompanyNavHost(companyViewModel : CompanyViewModel, authViewModel: AuthViewModel) {
	val navController = rememberNavController()
	
	NavHost(navController = navController, startDestination = CompanyRoutes.CHOOSE_COMPANY) {
		composable(CompanyRoutes.CHOOSE_COMPANY) {
			ChooseCompanyScreen(
				companyViewModel = companyViewModel,
				authViewModel = authViewModel,
				goCreateCompany = { navController.navigate(route = CompanyRoutes.CREATE_COMPANY)}
				)
		}
		composable(CompanyRoutes.CREATE_COMPANY) {
			CreateCompanyScreen(
				companyViewModel = companyViewModel,
				goChooseCompany = { navController.navigate(route = CompanyRoutes.CHOOSE_COMPANY) }
			)
		}
	}
}
