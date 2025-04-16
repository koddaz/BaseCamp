package com.basecampers.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.social.messaging.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.*

class ChatViewModel : ViewModel() {
	private val currentUserId = "current-user-123"
	
	// Mock data for chat info
	fun getChatInfo(chatId: String): Flow<ChatInfo?> = flow {
		// Mock chat data
		val chatInfo = ChatInfo(
			id = chatId,
			title = "Support Chat #$chatId",
			isActive = true
		)
		emit(chatInfo)
		
		// Later: Firebase implementation
		// db.collection("chats").document(chatId).get()
	}
	
	// Mock data for messages
	fun getMessages(chatId: String): Flow<List<Message>> = flow {
		// Generate some mock messages
		val messages = listOf(
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
			),
			Message(
				id = UUID.randomUUID().toString(),
				chatId = chatId,
				content = "Sure, I'd be happy to help with that. What's the issue you're experiencing?",
				senderId = "support-agent-123",
				senderName = "Support Agent",
				timestamp = System.currentTimeMillis() - 50 * 60 * 1000
			)
		)
		emit(messages)
		
		// Later: Firebase implementation
		// db.collection("chats").document(chatId).collection("messages")
		//   .orderBy("timestamp", Query.Direction.DESCENDING)
		//   .limit(50)
		//   .get()
	}
	
	fun getCurrentUserId(): String {
		return currentUserId
		// Later: Get from Firebase Auth or user session
	}
	
	fun sendMessage(chatId: String, content: String) {
		// Mock sending a message
		// Later: Add to Firestore
		// db.collection("chats").document(chatId).collection("messages").add(message)
	}
	
	fun closeChat(chatId: String) {
		// Mock closing a chat
		// Later: Update in Firestore
		// db.collection("chats").document(chatId).update("isActive", false)
	}
	
	fun deleteChat(chatId: String) {
		// Mock deleting a chat
		// Later: Delete from Firestore
		// db.collection("chats").document(chatId).delete()
	}
}

data class ChatInfo(
	val id: String = UUID.randomUUID().toString(),
	val title: String = "",
	val isActive: Boolean = true
)

data class Message(
	val id: String = UUID.randomUUID().toString(),
	val chatId: String = "",
	val content: String = "",
	val senderId: String = "",
	val senderName: String = "",
	val timestamp: Long = System.currentTimeMillis()
)