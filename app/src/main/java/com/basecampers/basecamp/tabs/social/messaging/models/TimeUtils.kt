package com.basecampers.basecamp.tabs.social.messaging.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for formatting timestamps in a human-readable way
 */
object TimeUtils {
	/**
	 * Formats a timestamp into a human-readable relative time string.
	 *
	 * @param timestamp Time in milliseconds since epoch
	 * @return Human-readable relative time string
	 */
	fun formatRelativeTime(timestamp: Long): String {
		val now = System.currentTimeMillis()
		val diff = now - timestamp
		
		return when {
			diff < 60 * 1000 -> "Just now"
			diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
			diff < 24 * 60 * 60 * 1000 -> {
				SimpleDateFormat("h:mm a", Locale.getDefault())
					.format(Date(timestamp))
			}
			diff < 48 * 60 * 60 * 1000 -> "Yesterday"
			else -> {
				SimpleDateFormat("MMM d", Locale.getDefault())
					.format(Date(timestamp))
			}
		}
	}
}