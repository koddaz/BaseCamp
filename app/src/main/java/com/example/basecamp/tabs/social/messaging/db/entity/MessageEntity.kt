package com.example.basecamp.tabs.social.messaging.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a message in a chat.
 */
@Entity(tableName = "messages")
data class MessageEntity(
	@PrimaryKey
	val id: String,
	val chatId: String,
	val senderId: String,
	val senderName: String,
	val content: String,
	val timestamp: Long,
	val status: String,
	val isSystemMessage: Boolean
)