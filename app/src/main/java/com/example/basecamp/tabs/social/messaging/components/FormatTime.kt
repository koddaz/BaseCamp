package com.example.basecamp.tabs.social.messaging.components

import java.text.SimpleDateFormat
import java.util.*

/**
 * Format a timestamp to a human-readable string
 */
fun formatTime(timestamp: Long): String {
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