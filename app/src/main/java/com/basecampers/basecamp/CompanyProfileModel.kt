package com.basecampers.basecamp

import java.net.URL

data class ProfileModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyList: List<String> = emptyList(),
)

data class CompanyProfileModel(
    val email : String = "No email",
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