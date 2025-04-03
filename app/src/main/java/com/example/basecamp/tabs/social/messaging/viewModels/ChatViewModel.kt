package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.example.basecamp.tabs.social.messaging.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.*

class ChatViewModel : ViewModel() {
	// Mock current user
	private val currentUserId = "user_1"
	private val currentUserName = "John Doe"
	
	// Mock super users
	private val superUsers = mapOf(
		"bb_1" to "Sarah (BaseBuddy)",
		"bb_2" to "Mike (BaseBuddy)"
	)
	
	// Mock chat data
	private val chatInfoMap = mutableMapOf(
		"chat1" to ChatInfo("chat1", "Chat with Sarah (BaseBuddy)"),
		"chat2" to ChatInfo("chat2", "Chat with Support"),
		"chat3" to ChatInfo("chat3", "Previous Support Chat"),
		"pending1" to ChatInfo("pending1", "Chat with John"),
		"pending2" to ChatInfo("pending2", "Chat with Emily"),
		"pending3" to ChatInfo("pending3", "Chat with Michael"),
		"active1" to ChatInfo("active1", "Chat with Lisa"),
		"active2" to ChatInfo("active2", "Chat with Robert")
	)
	
	// Mock messages data
	private val messagesMap = mutableMapOf<String, MutableStateFlow<List<Message>>>()
	
	init {
		// Initialize with some mock messages
		val defaultMessages = listOf(
			Message(
				id = "msg1",
				senderId = "bb_1",
				senderName = "Sarah (BaseBuddy)",
				receiverId = currentUserId,
				content = "Hello! How can I help you today?",
				timestamp = System.currentTimeMillis() - 600000, // 10 minutes ago
				isBaseBuddyMessage = true
			),
			Message(
				id = "msg2",
				senderId = currentUserId,
				senderName = currentUserName,
				receiverId = "bb_1",
				content = "I'm having trouble with booking a session.",
				timestamp = System.currentTimeMillis() - 540000 // 9 minutes ago
			)
		)
		
		// Set default messages for all chats for demo purposes
		chatInfoMap.keys.forEach { chatId ->
			messagesMap[chatId] = MutableStateFlow(defaultMessages)
		}
	}
	
	// Get current user ID
	fun getCurrentUserId(): String {
		return currentUserId
	}
	
	// Get chat information
	fun getChatInfo(chatId: String): Flow<ChatInfo?> {
		return flow {
			emit(chatInfoMap[chatId])
		}
	}
	
	// Get messages for a specific chat
	fun getMessages(chatId: String): Flow<List<Message>> {
		// Create a flow with messages if it doesn't exist
		if (!messagesMap.containsKey(chatId)) {
			messagesMap[chatId] = MutableStateFlow(emptyList())
		}
		return messagesMap[chatId]!!
	}
	
	// Send a message
	fun sendMessage(chatId: String, content: String) {
		if (content.isBlank()) return
		
		val messages = messagesMap[chatId] ?: MutableStateFlow(emptyList())
		
		// Determine the receiver ID (in a real app this would be more sophisticated)
		val receiverId = "bb_1" // Default to first BaseBuddy for demo
		
		val newMessage = Message(
			id = UUID.randomUUID().toString(),
			senderId = currentUserId,
			senderName = currentUserName,
			receiverId = receiverId,
			content = content,
			timestamp = System.currentTimeMillis()
		)
		
		messages.value = messages.value + newMessage
		
		// In a real app, this would also send the message to a backend
		
		// For demo, add an automatic reply after a short delay
		val chatInfo = chatInfoMap[chatId]
		if (chatInfo != null) {
			// In a real app, this would be handled by the backend
			// This is just for demo purposes
			val autoReply = Message(
				id = UUID.randomUUID().toString(),
				senderId = receiverId,
				senderName = superUsers[receiverId] ?: "BaseBuddy",
				receiverId = currentUserId,
				content = "Thanks for your message. I'll help you with that shortly.",
				timestamp = System.currentTimeMillis() + 1000, // 1 second later
				isBaseBuddyMessage = true
			)
			
			// Add auto-reply (in a real app this would come from the server)
			messages.value = messages.value + autoReply
		}
	}
	
	// Data class for chat info
	data class ChatInfo(
		val id: String,
		val title: String
	)
}