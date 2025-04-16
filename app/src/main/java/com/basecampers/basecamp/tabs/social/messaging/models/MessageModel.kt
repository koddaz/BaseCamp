package com.basecampers.basecamp.tabs.social.messaging.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Entity representing a single message within a chat conversation.
 * Designed for Room database storage with support for local caching
 * and background synchronization with Firebase.
 */
@Entity(tableName = "messages")
data class Message(
	/**
	 * Unique identifier for the message.
	 */
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),
	
	/**
	 * The ID of the chat this message belongs to.
	 * Used for database relationships and queries.
	 */
	val chatId: String = "",
	
	/**
	 * ID of the user who sent the message.
	 */
	val senderId: String = "",
	
	/**
	 * Display name of the sender.
	 */
	val senderName: String = "",
	
	/**
	 * The actual text content of the message.
	 */
	val content: String = "",
	
	/**
	 * When the message was created, as milliseconds since epoch.
	 */
	val timestamp: Long = System.currentTimeMillis(),
	
	/**
	 * Current delivery status of the message.
	 */
	val status: MessageStatus = MessageStatus.SENDING,
	
	/**
	 * Message type for special handling
	 */
	val type: MessageType = MessageType.TEXT,
	
	/**
	 * Flag indicating if this message has been synced to Firebase.
	 */
	val isSynced: Boolean = false,
	
	/**
	 * Optional metadata for extended functionality (e.g., attachments)
	 * Stored as a JSON string in Room and parsed when needed
	 */
	val metadataJson: String = "{}"
)