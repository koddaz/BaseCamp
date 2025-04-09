package com.example.basecamp.tabs.social.messaging.models

import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Represents a chat thread between users and BaseBuddies.
 */
data class Chat(
	val id: String = UUID.randomUUID().toString(),
	val title: String = "",
	val isActive: Boolean = true,
	val lastMessageTime: Long = System.currentTimeMillis(),
	val lastMessageText: String = "",
	val participantIds: List<String> = emptyList(),
	val unreadCount: Int = 0,
	
	// Additional properties needed based on the errors
	val userName: String = "", // Name of the other participant
	val isBaseBuddy: Boolean = false // Whether the other participant is a BaseBuddy
) {
	// Computed property for formatted time
	val time: String
		get() {
			val now = System.currentTimeMillis()
			val diff = now - lastMessageTime
			
			return when {
				diff < 60 * 1000 -> "Just now"
				diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
				diff < 24 * 60 * 60 * 1000 -> {
					SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(lastMessageTime))
				}
				diff < 48 * 60 * 60 * 1000 -> "Yesterday"
				else -> {
					SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(lastMessageTime))
				}
			}
		}
	
	// Alias for lastMessageText to match usage in UI
	val lastMessage: String
		get() = lastMessageText
	
	// Alias for title to match usage in UI
	val name: String
		get() = userName.ifEmpty { title }
}