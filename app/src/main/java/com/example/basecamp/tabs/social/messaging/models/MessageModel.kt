package com.example.basecamp.tabs.social.messaging.models

import java.util.UUID

/**
 * Represents a single message within a chat conversation.
 *
 * This model handles both user-generated messages and system notifications
 * within the chat interface. System messages have special handling with
 * "system" as senderId and "System" as senderName.
 */
data class Message(
	/**
	 * Unique identifier for the message.
	 * Automatically generated if not provided.
	 */
	val id: String = UUID.randomUUID().toString(),
	
	/**
	 * The ID of the chat this message belongs to.
	 * Used for database relationships and queries.
	 */
	val chatId: String,
	
	/**
	 * ID of the user who sent the message.
	 * For system-generated messages, this will be "system".
	 */
	val senderId: String,
	
	/**
	 * Display name of the sender.
	 * For system-generated messages, this will be "System".
	 */
	val senderName: String,
	
	/**
	 * The actual text content of the message.
	 */
	val content: String,
	
	/**
	 * When the message was created, as milliseconds since epoch.
	 * Automatically set to current time if not provided.
	 */
	val timestamp: Long = System.currentTimeMillis(),
	
	/**
	 * Current delivery status of the message.
	 * Tracks progress from sending to being read by recipient.
	 */
	val status: MessageStatus = MessageStatus.SENT,
	
	/**
	 * Whether this is an automated system notification rather than
	 * a message from a user. System messages are displayed differently
	 * in the UI and contain status updates about the chat.
	 */
	val isSystemMessage: Boolean = false
)

/**
 * Represents the delivery status of a message.
 */
enum class MessageStatus {
	/**
	 * Message is being uploaded to the server.
	 * Only exists locally until upload completes.
	 */
	SENDING,
	
	/**
	 * Message has been successfully uploaded to the server
	 * but may not have been delivered to the recipient yet.
	 */
	SENT,
	
	/**
	 * Message has been delivered to the recipient's device
	 * but they haven't viewed it yet.
	 */
	DELIVERED,
	
	/**
	 * Message has been viewed by the recipient.
	 */
	READ
}