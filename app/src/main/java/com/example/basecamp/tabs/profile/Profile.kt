package com.example.basecamp.tabs.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val email: String = ""
)
