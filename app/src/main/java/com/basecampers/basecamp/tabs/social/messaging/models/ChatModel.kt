package com.basecampers.basecamp.tabs.social.messaging.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Core model representing a chat conversation between users.
 * Used for storing conversation data in both Room and Firebase.
 */
@Entity(tableName = "chats")
data class Chat(
	/**
	 * Unique identifier for the chat.
	 */
	@PrimaryKey
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
	val subject: String = "",
	
	/**
	 * Flag indicating if the local cache is complete or needs refetching.
	 */
	val isCacheComplete: Boolean = false,
	
	/**
	 * Flag indicating if this record has been synced with Firebase.
	 */
	val isSynced: Boolean = false
) {
	/**
	 * Computed property: whether this chat is currently active
	 */
	val isActive: Boolean
		get() = status == ChatStatus.ACTIVE
	
	/**
	 * Computed property: title to display in the UI
	 */
	val title: String
		get() = subject.ifEmpty { "Chat #${id.takeLast(5)}" }
}
