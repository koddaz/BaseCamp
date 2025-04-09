package com.basecampers.navigation.Authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.authentication.ForgotPasswordScreen
import com.basecampers.basecamp.authentication.LoginScreen
import com.basecampers.basecamp.authentication.RegisterScreen
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel

@Composable
fun AuthNav(authViewModel : AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(authViewModel,
                goRegister = {
                    navController.navigate(route = "register")
            }, goForgotPass = {
                navController.navigate(route = "confirm")
            })
        }
        composable("register") {
            RegisterScreen(authViewModel ,
                goLogin = {
                    navController.navigate("login")
            })
        }
        composable("confirm") {
            ForgotPasswordScreen(authViewModel)
        }
    }
}