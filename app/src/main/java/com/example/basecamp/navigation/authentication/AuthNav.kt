package com.basecampers.navigation.Authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.Authentication.ConfirmScreen
import com.basecampers.Authentication.LoginScreen
import com.basecampers.Authentication.RegisterScreen

import com.example.basecamp.navigation.models.LoginModel
import kotlin.math.log

@Composable
fun AuthNav(loginmodel : LoginModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(loginmodel,
                goRegister = {
                    navController.navigate(route = "register")
            }, goForgotPass = {
                navController.navigate(route = "confirm")
            })
        }
        composable("register") {
            RegisterScreen(loginmodel ,
                goLogin = {
                    navController.navigate("login")
            })
        }
        composable("confirm") {
            ConfirmScreen(loginmodel)
        }
    }
}