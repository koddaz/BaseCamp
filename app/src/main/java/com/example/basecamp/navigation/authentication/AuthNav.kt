package com.example.navigation.Authentication

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Authentication.ForgotPasswordScreen
import com.example.Authentication.LoginScreen
import com.example.Authentication.RegisterScreen

import com.example.basecamp.navigation.models.AuthViewModel

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