package com.basecampers.basecamp.tabs.profile.models


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class ProfileModel(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val companyList: List<String> = emptyList()
)