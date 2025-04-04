package com.example.basecamp

import androidx.lifecycle.ViewModel
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.navigation.models.RegisterErrors
import java.net.URL

class UserViewModel : ViewModel() {
    private val userModel = UserModel(
        email = "",
        name = "",
        status = UserStatus.USER,
        imageUrl = URL("http://example.com/randomImage.png"),
        id = ""
    )

    fun isAdmin() : Boolean {
        return userModel.status == UserStatus.ADMIN
    }

    fun isSuperUser() : Boolean {
        return userModel.status == UserStatus.SUPER_USER
    }

    fun isUser() : Boolean {
        return userModel.status == UserStatus.USER
    }

    fun userStatus() {
        if(isAdmin()) {

        } else if(isSuperUser()) {

        } else {

        }

    }

}