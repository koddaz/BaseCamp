package com.example.basecamp.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.ForgotPasswordScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen
import com.example.basecamp.authentication.models.authRoutes
import com.example.basecamp.navigation.models.LoginModel

@Composable
fun AuthNavHost(loginModel : LoginModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = authRoutes.LOGIN) {
        composable(authRoutes.LOGIN) {
            LoginScreen(
                goRegister = { navController.navigate(route = authRoutes.REGISTER) },
                goForgotPass = { navController.navigate(route = authRoutes.FORGOTPASS) },
                loginModel = loginModel
            )
        }
        composable(authRoutes.REGISTER) {
            RegisterScreen(loginModel,
                goLogin = { navController.navigate(route = authRoutes.LOGIN) })
        }
        composable(authRoutes.FORGOTPASS) {
            ForgotPasswordScreen(loginModel)
        }
    }
}