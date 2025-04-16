package com.basecampers.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.social.messaging.models.Chat
import com.basecampers.basecamp.tabs.social.messaging.models.ChatInfo
import com.basecampers.basecamp.tabs.social.messaging.models.ChatStatus
import com.basecampers.basecamp.tabs.social.messaging.models.Participant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class SuperUserMessagingViewModel : ViewModel() {
	// Mock active chats
	private val _activeChats = MutableStateFlow<List<ChatInfo>>(
		listOf(
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Support for Alex",
				isActive = true,
				lastMessageText = "I'll check that for you right away",
				lastMessageTime = System.currentTimeMillis() - 10 * 60 * 1000, // 10 min ago
				unreadCount = 0,
				isRead = true
			),
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Help with Booking",
				isActive = true,
				lastMessageText = "Your booking has been updated successfully",
				lastMessageTime = System.currentTimeMillis() - 3 * 60 * 60 * 1000, // 3 hours ago
				unreadCount = 1,
				isRead = false
			),
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Membership Question",
				isActive = false,
				lastMessageText = "Thank you for your help!",
				lastMessageTime = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
				unreadCount = 0,
				isRead = true
			)
		)
	)
	val activeChats: StateFlow<List<ChatInfo>> = _activeChats.asStateFlow()
	
	// Mock pending chat requests (now using Chat with PENDING status)
	private val _pendingChats = MutableStateFlow<List<ChatInfo>>(
		listOf(
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "App Functionality - Taylor Smith",
				isActive = false,
				lastMessageText = "I'm having trouble with the payment system in the app",
				lastMessageTime = System.currentTimeMillis() - 20 * 60 * 1000, // 20 min ago
				unreadCount = 1,
				isRead = false
			),
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Booking Issue - Alex Johnson",
				isActive = false,
				lastMessageText = "Hello, I need help with a booking that was canceled automatically",
				lastMessageTime = System.currentTimeMillis() - 5 * 60 * 1000, // 5 min ago
				unreadCount = 1,
				isRead = false
			)
		)
	)
	val pendingChats: StateFlow<List<ChatInfo>> = _pendingChats.asStateFlow()
	
	// Internal storage for full chat objects
	private val _allChats = MutableStateFlow<Map<String, Chat>>(
		// Would be populated from Firestore in a real implementation
		mapOf()
	)
	
	// Get a specific chat by ID
	fun getPendingChatById(chatId: String): ChatInfo? {
		return pendingChats.value.find { it.id == chatId }
	}
	
	// Get details of a chat
	fun getChatDetails(chatId: String) = MutableStateFlow(
		_allChats.value[chatId]
	)
	
	// Get the first message (for pending chats)
	fun getChatRequestDetails(chatId: String) = MutableStateFlow(
		pendingChats.value.find { it.id == chatId }?.let {
			mapOf("message" to it.lastMessageText)
		}
	)
	
	// Mark a chat as read
	fun markChatAsRead(chatId: String) {
		// Update the local unread count
		_pendingChats.update { chats ->
			chats.map {
				if (it.id == chatId) it.copy(unreadCount = 0, isRead = true) else it
			}
		}
		
		_activeChats.update { chats ->
			chats.map {
				if (it.id == chatId) it.copy(unreadCount = 0, isRead = true) else it
			}
		}
		
		// In a real implementation, update in Firestore
	}
	
	// Accept a chat request
	fun acceptChatRequest(chatId: String) {
		val pendingChat = pendingChats.value.find { it.id == chatId } ?: return
		
		// Move from pending to active
		_pendingChats.update { current ->
			current.filter { it.id != chatId }
		}
		
		// Add to active chats
		_activeChats.update { current ->
			listOf(
				pendingChat.copy(
					isActive = true
				)
			) + current
		}
		
		// In a real implementation:
		// 1. Update chat status to ACTIVE in Firestore
		// 2. Add current user as participant
	}
	
	// Decline a chat request
	fun declineChatRequest(chatId: String) {
		// Remove from pending
		_pendingChats.update { current ->
			current.filter { it.id != chatId }
		}
		
		// In a real implementation:
		// Update chat status to DECLINED in Firestore
	}
	
	// Get the current super user ID
	fun getCurrentUserId(): String {
		return "current-super-user-id" // Replace with actual user ID from auth
	}
}