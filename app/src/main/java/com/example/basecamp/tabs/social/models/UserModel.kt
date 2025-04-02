package com.example.basecamp.tabs.social.models

data class User(
	val id: String,
	val name: String,
	val role: UserRole = UserRole.USER
)