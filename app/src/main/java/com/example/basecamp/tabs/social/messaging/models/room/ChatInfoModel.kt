package com.example.basecamp.tabs.social.messaging.models.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.basecamp.tabs.social.messaging.models.ChatStatus

/**
 * Lightweight representation of a chat for displaying in lists.
 * Used in Room database for efficient UI rendering.
 */
@Entity(tableName = "chat_info")
data class ChatInfo(
	/**
	 * Unique identifier - matches the Firebase chat ID.
	 */
	@PrimaryKey
	val id: String,
	
	/**
	 * Display title of the chat, generated based on user role.
	 * - For regular users: "Chat with [SuperUser name]"
	 * - For SuperUsers: "Chat with [User name]"
	 */
	val title: String,
	
	/**
	 * Current state of the chat.
	 */
	val status: ChatStatus,
	
	/**
	 * When the chat was created.
	 */
	val createdAt: Long,
	
	/**
	 * Timestamp of the most recent message.
	 */
	val lastMessageTime: Long,
	
	/**
	 * Preview text of the most recent message.
	 */
	val lastMessageText: String,
	
	/**
	 * ID of the user who initiated the chat.
	 */
	val creatorId: String,
	
	/**
	 * ID of the SuperUser assigned to this chat, or null if pending.
	 */
	val assignedToId: String?,
	
	/**
	 * Number of unread messages for the current user.
	 */
	val unreadCount: Int = 0,
	
	/**
	 * Whether this chat is assigned to the current user.
	 */
	val isAssignedToCurrentUser: Boolean = false,
	
	/**
	 * Original topic/subject of the chat request.
	 */
	val subject: String
)