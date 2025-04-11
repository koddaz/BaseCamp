package com.basecampers.basecamp.tabs.profile.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
//HÄR!!!
data class ProfileModel(
    @PrimaryKey val id: String = "",
    val email : String,
    val userName : String = "No name yet",
    val imageUrl : String?,
    val bio : String = "No bio yet",
    val status : UserStatus,
    val companyName : String = "No name yet",
)

data class CompanyModel(
    val companyName : String = "No name yet",
    val ownerUID : String,
    val bio : String = "No bio yet",
    val imageUrl : String?

)

enum class UserStatus {
    ADMIN,
    SUPER_USER,
    USER
}