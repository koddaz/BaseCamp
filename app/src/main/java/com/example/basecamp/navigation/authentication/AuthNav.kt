package com.basecampers.navigation.Authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.ConfirmScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen

import com.example.basecamp.navigation.models.LoginModel

@Composable
fun AuthNav(loginModel : LoginModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(loginModel,
                goRegister = {
                    navController.navigate(route = "register")
            }, goForgotPass = {
                navController.navigate(route = "confirm")
            })
        }
        composable("register") {
            RegisterScreen(loginModel ,
                goLogin = {
                    navController.navigate("login")
            })
        }
        composable("confirm") {
            ConfirmScreen(loginModel)
        }
    }
}