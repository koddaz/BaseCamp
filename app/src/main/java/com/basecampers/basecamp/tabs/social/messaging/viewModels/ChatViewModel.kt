package com.basecampers.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.social.messaging.models.ChatInfo
import com.basecampers.basecamp.tabs.social.messaging.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class ChatViewModel : ViewModel() {
	private val currentUserId = "current-user-123"
	
	// Simple placeholder for chat info
	fun getChatInfo(chatId: String): Flow<ChatInfo?> = flowOf(
		ChatInfo(
			id = chatId,
			title = "Support Chat",
			isActive = true
		)
	)
	
	// Simple placeholder for messages
	fun getMessages(chatId: String): Flow<List<Message>> = flowOf(
		listOf(
			Message(
				id = UUID.randomUUID().toString(),
				chatId = chatId,
				content = "Hello, how can I help you today?",
				senderId = "support-agent-123",
				senderName = "Support Agent",
				timestamp = System.currentTimeMillis() - 60 * 60 * 1000
			),
			Message(
				id = UUID.randomUUID().toString(),
				chatId = chatId,
				content = "I have a question about my booking.",
				senderId = currentUserId,
				senderName = "You",
				timestamp = System.currentTimeMillis() - 55 * 60 * 1000
			)
		)
	)
	
	fun getCurrentUserId(): String = currentUserId
	
	fun sendMessage(chatId: String, content: String) {
		// Placeholder - no implementation needed for UI testing
	}
	
	fun closeChat(chatId: String) {
		// Placeholder - no implementation needed for UI testing
	}
	
	fun deleteChat(chatId: String) {
		// Placeholder - no implementation needed for UI testing
	}
}