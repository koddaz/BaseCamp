package com.basecampers.basecamp.tabs.social.messaging.models

import java.util.UUID

/**
 * Lightweight representation of a chat used for header information.
 */
data class ChatInfo(
	val id: String = UUID.randomUUID().toString(),
	val title: String = "",
	val isActive: Boolean = true,
	val isRead: Boolean = true
)