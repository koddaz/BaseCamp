package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.example.basecamp.tabs.social.models.Chat
import com.example.basecamp.tabs.social.models.Message
import com.example.basecamp.tabs.social.models.User
import com.example.basecamp.tabs.social.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.util.*

class SuperUserMessagingViewModel : ViewModel() {
	// Mock current super user
	private val currentSuperUserId = "bb_1"
	
	// Mock data for pending chat requests
	private val _pendingChats = MutableStateFlow(
		listOf(
			PendingChat(
				id = "pending1",
				userName = "John",
				subject = "Booking Issue",
				timeReceived = "5 minutes ago",
				isRead = false
			),
			PendingChat(
				id = "pending2",
				userName = "Emily",
				subject = "App Functionality",
				timeReceived = "20 minutes ago",
				isRead = false
			),
			PendingChat(
				id = "pending3",
				userName = "Michael",
				subject = "Facility Maintenance",
				timeReceived = "1 hour ago",
				isRead = true
			)
		)
	)
	val pendingChats: StateFlow<List<PendingChat>> = _pendingChats.asStateFlow()
	
	// Mock data for active chats
	private val _activeChats = MutableStateFlow(
		listOf(
			ActiveChat(
				id = "active1",
				userName = "Lisa",
				lastMessageTime = "2 minutes ago"
			),
			ActiveChat(
				id = "active2",
				userName = "Robert",
				lastMessageTime = "Yesterday"
			)
		)
	)
	val activeChats: StateFlow<List<ActiveChat>> = _activeChats.asStateFlow()
	
	// Get a pending chat by ID
	fun getPendingChatById(chatId: String): PendingChat? {
		return _pendingChats.value.find { it.id == chatId }
	}
	
	// Get detailed chat request information
	fun getChatRequestDetails(chatId: String): Flow<ChatRequestDetails?> {
		// In a real app, this would fetch from a database
		return flow {
			val pendingChat = _pendingChats.value.find { it.id == chatId }
			if (pendingChat != null) {
				emit(
					ChatRequestDetails(
						id = pendingChat.id,
						userName = pendingChat.userName,
						subject = pendingChat.subject,
						message = "Hello, I need help with ${pendingChat.subject.lowercase()}. Can you assist me?",
						timeReceived = pendingChat.timeReceived
					)
				)
			} else {
				emit(null)
			}
		}
	}
	
	// Mark chat as read
	fun markChatAsRead(chatId: String) {
		_pendingChats.value = _pendingChats.value.map {
			if (it.id == chatId) it.copy(isRead = true) else it
		}
	}
	
	// Accept a chat request
	fun acceptChatRequest(chatId: String) {
		val pendingChat = _pendingChats.value.find { it.id == chatId }
		
		if (pendingChat != null) {
			// Remove from pending
			_pendingChats.value = _pendingChats.value.filter { it.id != chatId }
			
			// Add to active
			val activeChat = ActiveChat(
				id = chatId,
				userName = pendingChat.userName,
				lastMessageTime = "Just now"
			)
			
			_activeChats.value = listOf(activeChat) + _activeChats.value
		}
	}
	
	// Decline a chat request
	fun declineChatRequest(chatId: String) {
		_pendingChats.value = _pendingChats.value.filter { it.id != chatId }
	}
	
	// Close an active chat
	fun closeChat(chatId: String) {
		_activeChats.value = _activeChats.value.filter { it.id != chatId }
	}
	
	// Data classes for Super User messaging
	data class PendingChat(
		val id: String,
		val userName: String,
		val subject: String,
		val timeReceived: String,
		val isRead: Boolean
	)
	
	data class ActiveChat(
		val id: String,
		val userName: String,
		val lastMessageTime: String
	)
	
	data class ChatRequestDetails(
		val id: String,
		val userName: String,
		val subject: String,
		val message: String,
		val timeReceived: String
	)
}