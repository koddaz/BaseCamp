package com.example.basecamp.authentication



import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.Authentication.LoginScreen
import com.example.basecamp.aRootFolder.Root
import com.example.basecamp.navigation.models.LoginModel


@Composable
fun LoginStart(loginmodel : LoginModel = viewModel()) {

    val loggedin by loginmodel.loggedin.collectAsState()

    if(loggedin) {
       Root()
    } else {
        LoginScreen(loginmodel, goRegister = {}, goConfirm = {})
    }
}