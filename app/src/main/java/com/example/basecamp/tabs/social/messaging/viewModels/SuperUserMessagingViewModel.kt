package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.example.basecamp.tabs.social.messaging.models.Chat
import com.example.basecamp.tabs.social.messaging.models.ChatRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class SuperUserMessagingViewModel : ViewModel() {
	// Mock active chats
	private val _activeChats = MutableStateFlow<List<Chat>>(
		listOf(
			Chat(
				title = "Support for Alex",
				isActive = true,
				lastMessageText = "I'll check that for you right away",
				lastMessageTime = System.currentTimeMillis() - 10 * 60 * 1000, // 10 min ago
				unreadCount = 0
			),
			Chat(
				title = "Help with Booking",
				isActive = true,
				lastMessageText = "Your booking has been updated successfully",
				lastMessageTime = System.currentTimeMillis() - 3 * 60 * 60 * 1000, // 3 hours ago
				unreadCount = 1
			),
			Chat(
				title = "Membership Question",
				isActive = false,
				lastMessageText = "Thank you for your help!",
				lastMessageTime = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
				unreadCount = 0
			)
		)
	)
	val activeChats: StateFlow<List<Chat>> = _activeChats.asStateFlow()
	
	// Mock pending chat requests
	private val _pendingChats = MutableStateFlow<List<ChatRequest>>(
		listOf(
			ChatRequest(
				userId = "user-1",
				userName = "Taylor Smith",
				subject = "App Functionality",
				message = "I'm having trouble with the payment system in the app",
				timestamp = System.currentTimeMillis() - 20 * 60 * 1000 // 20 min ago
			),
			ChatRequest(
				userId = "user-2",
				userName = "Alex Johnson",
				subject = "Booking Issue",
				message = "Hello, I need help with a booking that was canceled automatically",
				timestamp = System.currentTimeMillis() - 5 * 60 * 1000 // 5 min ago
			)
		)
	)
	val pendingChats: StateFlow<List<ChatRequest>> = _pendingChats.asStateFlow()
	
	// Get a specific chat request by ID
	fun getPendingChatById(requestId: String): ChatRequest? {
		return pendingChats.value.find { it.id == requestId }
	}
	
	// Get details of a chat request
	fun getChatRequestDetails(requestId: String) = MutableStateFlow(
		pendingChats.value.find { it.id == requestId }
	)
	
	// Mark a chat request as read
	fun markChatAsRead(chatId: String) {
		// In a real implementation, update in Firestore
	}
	
	// Accept a chat request
	fun acceptChatRequest(requestId: String) {
		val request = pendingChats.value.find { it.id == requestId } ?: return
		
		// Create a new chat from the request
		val newChat = Chat(
			title = "Support for ${request.userName}",
			isActive = true,
			lastMessageText = request.message,
			lastMessageTime = request.timestamp,
			participantIds = listOf(request.userId, "current-super-user-id")
		)
		
		// Add to active chats
		_activeChats.update { current ->
			listOf(newChat) + current
		}
		
		// Remove from pending
		_pendingChats.update { current ->
			current.filter { it.id != requestId }
		}
		
		// In a real implementation, update in Firestore
	}
	
	// Decline a chat request
	fun declineChatRequest(requestId: String) {
		// Remove from pending
		_pendingChats.update { current ->
			current.filter { it.id != requestId }
		}
		
		// In a real implementation, update in Firestore
	}
	
	// Get the current super user ID
	fun getCurrentUserId(): String {
		return "current-super-user-id" // Replace with actual user ID from auth
	}
}