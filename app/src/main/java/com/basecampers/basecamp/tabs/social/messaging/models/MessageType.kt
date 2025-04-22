package com.basecampers.basecamp.tabs.social.messaging.models

/**
 * Types of messages supported by the app
 */
enum class MessageType {
	/**
	 * Standard text message
	 */
	TEXT,
	
	/**
	 * System-generated notification about chat events
	 */
	SYSTEM_NOTIFICATION,
	
	/**
	 * Message containing an image attachment
	 */
	IMAGE,
	
	/**
	 * Message containing a file attachment
	 */
	FILE,
	
	/**
	 * Message containing a URL with preview
	 */
	LINK
}