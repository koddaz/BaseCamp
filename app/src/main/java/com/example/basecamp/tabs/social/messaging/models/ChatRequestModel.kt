package com.example.basecamp.tabs.social.messaging.models

import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatRequest(
	val id: String = UUID.randomUUID().toString(),
	val userId: String,
	val userName: String,
	val subject: String,
	val message: String,
	val timestamp: Long = System.currentTimeMillis(),
	val isAssigned: Boolean = false,
	val assignedTo: String? = null
) {
	// Computed property for formatted time
	val timeReceived: String
		get() {
			val now = System.currentTimeMillis()
			val diff = now - timestamp
			
			return when {
				diff < 60 * 1000 -> "Just now"
				diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
				diff < 24 * 60 * 60 * 1000 -> {
					SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
				}
				diff < 48 * 60 * 60 * 1000 -> "Yesterday"
				else -> {
					SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
				}
			}
		}
}