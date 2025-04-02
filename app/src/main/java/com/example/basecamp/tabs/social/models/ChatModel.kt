package com.example.basecamp.tabs.social.models

import java.util.UUID

data class Chat(
	val id: String = UUID.randomUUID().toString(),
	val participants: List<String>, // User IDs
	val lastMessage: Message? = null,
	val isActive: Boolean = true,
	val unreadCount: Int = 0
)