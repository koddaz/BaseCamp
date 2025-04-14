package com.basecampers.basecamp.tabs.profile.models

import java.net.URL


data class CompanyProfileModel(
    val imageUrl : URL? = null,
    val bio : String = "No bio yet",
    val status : UserStatus = UserStatus.USER,
    val id : String = "",
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