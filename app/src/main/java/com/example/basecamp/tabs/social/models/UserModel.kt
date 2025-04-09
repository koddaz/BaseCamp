package com.example.basecamp.tabs.social.models

import java.util.UUID

data class User(
	val id: String = UUID.randomUUID().toString(),
	val name: String,
	val role: UserRole = UserRole.USER
)