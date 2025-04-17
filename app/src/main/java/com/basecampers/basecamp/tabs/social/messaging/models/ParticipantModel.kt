package com.basecampers.basecamp.tabs.social.messaging.models

import com.basecampers.basecamp.tabs.profile.models.UserStatus

/**
 * Represents a participant in a chat conversation.
 * Tracks user-specific information like unread count.
 */
data class Participant(
	/**
	 * User identifier of the participant.
	 */
	val userId: String = "",
	
	/**
	 * Display name shown in the chat interface.
	 */
	val displayName: String = "",
	
	/**
	 * Role of the participant (USER, SUPER_USER, ADMIN).
	 */
	val role: UserStatus = UserStatus.USER,
	
	/**
	 * Number of unread messages for this participant.
	 * Incremented when new messages arrive and reset when the chat is viewed.
	 */
	val unreadCount: Int = 0,
	
	/**
	 * Timestamp of the last message this participant has read.
	 * Used to determine which messages should be marked as new.
	 */
	val lastReadTimestamp: Long = 0
)