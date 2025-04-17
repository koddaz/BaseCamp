package com.basecampers.basecamp.tabs.social.messaging.models

/**
 * Delivery status of a message
 */
enum class MessageStatus {
	/**
	 * Message is being created locally but not yet sent to server
	 */
	SENDING,
	
	/**
	 * Message has been successfully uploaded to the server
	 */
	SENT,
	
	/**
	 * Message has been delivered to recipient's device
	 */
	DELIVERED,
	
	/**
	 * Message has been viewed by the recipient
	 */
	READ,
	
	/**
	 * Message failed to send due to an error
	 */
	FAILED
}