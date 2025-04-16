package com.basecampers.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.social.messaging.models.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class UserMessagingViewModel : ViewModel() {
	// Mock data for testing
	private val _chats = MutableStateFlow<List<Chat>>(
		listOf(
			Chat(
				title = "Support Chat",
				isActive = true,
				lastMessageText = "Thanks for your help!",
				lastMessageTime = System.currentTimeMillis() - 30 * 60 * 1000, // 30 min ago
				unreadCount = 2
			),
			Chat(
				title = "Booking Issue",
				isActive = false,
				lastMessageText = "Issue has been resolved.",
				lastMessageTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000, // 1 day ago
			),
			Chat(
				title = "Facility Question",
				isActive = false,
				lastMessageText = "Thank you for using our service.",
				lastMessageTime = System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
			)
		)
	)
	val chats = _chats.asStateFlow()
	
	// User function
	fun createChatRequest(topic: String, message: String): String {
		val chatId = UUID.randomUUID().toString()
		
		// In a real implementation, this would create a ChatRequest document in Firestore
		// For now, we'll simulate by creating a Chat directly
		val newChat = Chat(
			id = chatId,
			title = topic,
			isActive = true,
			lastMessageText = message,
			lastMessageTime = System.currentTimeMillis()
		)
		
		_chats.update { currentChats ->
			listOf(newChat) + currentChats
		}
		
		return chatId
	}
	
	// Will be replaced with Firebase implementation
	fun loadChats() {
		// Already loaded with mock data for now
		// Later: db.collection("chats").whereArrayContains("participantIds", currentUserId)
	}
	
	fun startNewChat(topic: String, message: String): String {
		val chatId = UUID.randomUUID().toString()
		val newChat = Chat(
			id = chatId,
			title = topic,
			isActive = true,
			lastMessageText = message,
			lastMessageTime = System.currentTimeMillis()
		)
		
		_chats.update { currentChats ->
			listOf(newChat) + currentChats
		}
		
		// Later: Create in Firestore
		return chatId
	}
	
	fun closeChat(chatId: String) {
		_chats.update { currentChats ->
			currentChats.map { chat ->
				if (chat.id == chatId) chat.copy(isActive = false) else chat
			}
		}
		
		// Later: Update in Firestore
	}
	
	fun deleteChat(chatId: String) {
		_chats.update { currentChats ->
			currentChats.filter { it.id != chatId }
		}
		
		// Later: Delete from Firestore
	}
}