package com.example.basecamp

import java.net.URL


data class UserModel(
    val email : String,
    val name : String = "No name yet",
    val imageUrl : URL?,
    val bio : String = "No bio yet",
    val status : UserStatus,
    val id : String,
    val companyName : String = "No name yet",
) {

}

data class CompanyModel(
    val companyName : String = "No name yet",
    val ownerUID : String,
    val bio : String = "No bio yet",
    val imageUrl : URL?
)

enum class UserStatus {
    ADMIN,
    SUPER_USER,
    USER
}