package com.basecampers.basecamp.tabs.social.messaging.models

/**
 * Lightweight representation of a chat used for header information
 * and list displays. Contains only essential data for UI rendering.
 */
data class ChatInfo(
	/**
	 * Unique identifier for the chat.
	 */
	val id: String = "",
	
	/**
	 * Display title for the chat.
	 */
	val title: String = "",
	
	/**
	 * Whether this chat is currently in active state.
	 */
	val isActive: Boolean = true,
	
	/**
	 * Whether all messages have been read by the current user.
	 */
	val isRead: Boolean = true,
	
	/**
	 * Number of unread messages for the current user.
	 */
	val unreadCount: Int = 0,
	
	/**
	 * Preview text from the most recent message.
	 */
	val lastMessageText: String = "",
	
	/**
	 * Timestamp of the most recent message.
	 */
	val lastMessageTime: Long = 0
)