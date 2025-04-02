package com.example.basecamp.tabs.social.models

import java.util.UUID

data class Message(
	val id: String = UUID.randomUUID().toString(),
	val senderId: String,
	val senderName: String,
	val receiverId: String,
	val content: String,
	val timestamp: Long = System.currentTimeMillis(),
	val isRead: Boolean = false,
	val isBaseBuddyMessage: Boolean = false
)