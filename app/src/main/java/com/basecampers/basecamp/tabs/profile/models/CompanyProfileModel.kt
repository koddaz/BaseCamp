package com.basecampers.basecamp.tabs.profile.models

import java.net.URL

data class CompanyProfileModel(
    val id: String = "",
    val companyId: String = "",
    val bio: String = "No bio yet",
    val status: UserStatus = UserStatus.USER,
    val imageUrl: URL? = null
)

data class CompanyModel(
    val companyId: String = "",
    val companyName: String = "No name yet",
    val ownerUID: String = "",
    val bio: String = "No bio yet",
    val imageUrl: URL? = null
)

enum class UserStatus {
    ADMIN,
    SUPER_USER,
    USER
}