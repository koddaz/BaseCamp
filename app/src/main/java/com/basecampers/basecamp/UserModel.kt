package com.basecampers.basecamp

import java.net.URL


data class UserModel(
    val email : String = "No email",
    val firstName : String = "No name yet",
    val lastName : String = "No name yet",
    val imageUrl : URL? = null,
    val bio : String = "No bio yet",
    val status : UserStatus = UserStatus.USER,
    val id : String = "No id",
    val companyName : String = "No name yet",
    val companyId: String = ""
)

data class CompanyModel(
    val companyName : String = "No name yet",
    val ownerUID : String,
    val companyId: String = "",
    val bio : String = "No bio yet",
    val imageUrl : URL?
)

enum class UserStatus {
    ADMIN,
    SUPER_USER,
    USER
}