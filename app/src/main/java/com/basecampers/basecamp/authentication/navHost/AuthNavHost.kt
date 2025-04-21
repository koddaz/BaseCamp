package com.basecampers.basecamp.authentication.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.ForgotPasswordScreen
import com.basecampers.basecamp.authentication.LoginScreen
import com.basecampers.basecamp.authentication.RegisterScreen
import com.basecampers.basecamp.authentication.models.AuthRoutes
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@Composable
fun AuthNavHost(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AuthRoutes.REGISTER) {
        composable(AuthRoutes.LOGIN) {
            LoginScreen(
                goRegister = { navController.navigate(route = AuthRoutes.REGISTER) },
                goForgotPass = { navController.navigate(route = AuthRoutes.FORGOTPASS) },
                authViewModel = authViewModel
            )
        }
        composable(AuthRoutes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                goLogin = { navController.navigate(route = AuthRoutes.LOGIN) })
        }
        composable(AuthRoutes.FORGOTPASS) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBackClick = { 
                    navController.popBackStack(
                        route = AuthRoutes.LOGIN,
                        inclusive = false
                    )
                }
            )
        }
    }
}