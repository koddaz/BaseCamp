package com.basecampers.basecamp.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.basecampers.basecamp.authentication.models.authRoutes
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel

import com.basecampers.Authentication.ForgotPasswordScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen

@Composable
fun AuthNavHost(authViewModel : AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = authRoutes.REGISTER) {
        composable(authRoutes.LOGIN) {
            LoginScreen(
                goRegister = { navController.navigate(route = authRoutes.REGISTER) },
                goForgotPass = { navController.navigate(route = authRoutes.FORGOTPASS) },
                authViewModel = authViewModel
            )
        }
        composable(authRoutes.REGISTER) {
            RegisterScreen(authViewModel,
                goLogin = { navController.navigate(route = authRoutes.LOGIN) })
        }
        composable(authRoutes.FORGOTPASS) {
            ForgotPasswordScreen(authViewModel)
        }
    }
}