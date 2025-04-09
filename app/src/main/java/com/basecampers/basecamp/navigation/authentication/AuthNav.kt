package com.basecampers.navigation.Authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.ForgotPasswordScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen

import com.basecampers.basecamp.navigation.models.AuthViewModel

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