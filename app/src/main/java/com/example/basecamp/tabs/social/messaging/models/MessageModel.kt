package com.example.basecamp.tabs.social.messaging.models

import java.util.UUID

/**
 * Represents a single message within a chat.
 *
 * This combines both Message models, keeping the chatId for organization
 * and adding isRead/isBaseBuddyMessage for functionality.
 */
data class Message(
	val id: String = UUID.randomUUID().toString(),
	val chatId: String = "",
	val content: String = "",
	val senderId: String = "",
	val senderName: String = "",
	val receiverId: String = "",
	val timestamp: Long = System.currentTimeMillis(),
	val isRead: Boolean = false,
	val isBaseBuddyMessage: Boolean = false
)