package com.example.basecamp.tabs.social.messaging.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing chat information.
 */
@Entity(tableName = "chat_info")
data class ChatInfoEntity(
	@PrimaryKey
	val id: String,
	val companyId: String,
	val title: String,
	val status: String,
	val createdAt: Long,
	val lastMessageTime: Long,
	val lastMessageText: String,
	val creatorId: String,
	val assignedToId: String?,
	val subject: String,
	val unreadCount: Int
)