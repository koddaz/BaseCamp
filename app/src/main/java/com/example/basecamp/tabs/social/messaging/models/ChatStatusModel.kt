package com.example.basecamp.tabs.social.messaging.models

/**
 * Represents the current state of a chat conversation.
 */
enum class ChatStatus {
	/**
	 * Initial state when a user creates a chat request.
	 * Waiting for a SuperUser to be assigned.
	 */
	PENDING,
	
	/**
	 * Conversation is ongoing between participants.
	 * A SuperUser has been assigned and communication is active.
	 */
	ACTIVE,
	
	/**
	 * Conversation has been marked as resolved or ended.
	 * No new messages can be sent, but history remains visible.
	 */
	CLOSED,
	
	/**
	 * Chat has been marked for deletion.
	 * May be completely removed during database cleanup.
	 */
	DELETED
}