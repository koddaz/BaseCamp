package com.example.basecamp.tabs.social.messaging.models

import java.util.UUID

/**
 * Core model representing a chat conversation between users.
 * Used for storing conversation data in Firebase.
 */
data class Chat(
	/**
	 * Unique identifier for the chat.
	 */
	val id: String = UUID.randomUUID().toString(),
	
	/**
	 * Current state of the chat (PENDING, ACTIVE, CLOSED, DELETED).
	 */
	val status: ChatStatus = ChatStatus.PENDING,
	
	/**
	 * When the chat was created.
	 */
	val createdAt: Long = System.currentTimeMillis(),
	
	/**
	 * Timestamp of the most recent message.
	 */
	val lastMessageTime: Long = System.currentTimeMillis(),
	
	/**
	 * Preview text of the most recent message.
	 */
	val lastMessageText: String = "",
	
	/**
	 * List of all users participating in this chat.
	 */
	val participants: List<Participant> = emptyList(),
	
	/**
	 * ID of the user who initiated the chat.
	 */
	val creatorId: String = "",
	
	/**
	 * ID of the SuperUser assigned to this chat, or null if pending.
	 */
	val assignedToId: String? = null,
	
	/**
	 * Original topic/subject of the chat request.
	 */
	val subject: String = ""
)