package com.basecampers.basecamp.tabs.profile.models


data class ProfileModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyList: List<String> = emptyList(),
)

data class CombinedProfileModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyList: List<String> = emptyList(),
    val bio: String = "",
    val status: UserStatus = UserStatus.USER,
    val companyName: String = ""
)