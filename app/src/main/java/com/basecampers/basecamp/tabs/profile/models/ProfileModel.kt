package com.basecampers.basecamp.tabs.profile.models


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class ProfileModel(
    val userId: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyList: List<String> = emptyList(),
    // Add Room primary key
    @PrimaryKey
    val id: String = userId
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