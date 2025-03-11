package com.example.basecamp.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.ConfirmScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen
import com.example.basecamp.authentication.models.authRoutes
import com.example.basecamp.navigation.models.LoginModel
import com.example.basecamp.tabs.home.HomeScreen
import com.example.basecamp.tabs.profile.models.profileRoutes

@Composable
fun AuthNavHost(loginmodel : LoginModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = authRoutes.LOGIN) {
        composable(authRoutes.LOGIN) {
            LoginScreen(
                goRegister = { navController.navigate(route = authRoutes.REGISTER)},
                goForgotPass = { navController.navigate(route = authRoutes.FORGOTPASS)}
            ) }
        composable(authRoutes.REGISTER) {
            RegisterScreen(loginmodel,
                goLogin = {navController.navigate(route = authRoutes.LOGIN)})
        }
        composable(authRoutes.FORGOTPASS) {
            ConfirmScreen()
        }
    }

}