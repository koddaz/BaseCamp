package com.example.basecamp.tabs.social.messaging.db.entity

import androidx.room.Entity

/**
 * Room entity representing a chat participant.
 */
@Entity(
	tableName = "chat_participants",
	primaryKeys = ["chatId", "userId"]
)
data class ChatParticipantEntity(
	val chatId: String,
	val userId: String,
	val displayName: String,
	val role: String,
	val unreadCount: Int,
	val lastReadTimestamp: Long
)