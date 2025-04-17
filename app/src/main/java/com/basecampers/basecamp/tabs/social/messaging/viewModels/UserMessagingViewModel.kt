package com.basecampers.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.social.messaging.models.Chat
import com.basecampers.basecamp.tabs.social.messaging.models.ChatInfo
import com.basecampers.basecamp.tabs.social.messaging.models.ChatStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*

class UserMessagingViewModel : ViewModel() {
	// Mock data for testing
	private val _chats = MutableStateFlow<List<ChatInfo>>(
		listOf(
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Support Chat",
				isActive = true,
				lastMessageText = "Thanks for your help!",
				lastMessageTime = System.currentTimeMillis() - 30 * 60 * 1000, // 30 min ago
				unreadCount = 2,
				isRead = false
			),
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Booking Issue",
				isActive = false,
				lastMessageText = "Issue has been resolved.",
				lastMessageTime = System.currentTimeMillis() - 24 * 60 * 60 * 1000, // 1 day ago
				unreadCount = 0,
				isRead = true
			),
			ChatInfo(
				id = UUID.randomUUID().toString(),
				title = "Facility Question",
				isActive = false,
				lastMessageText = "Thank you for using our service.",
				lastMessageTime = System.currentTimeMillis() - 48 * 60 * 60 * 1000, // 2 days ago
				unreadCount = 0,
				isRead = true
			)
		)
	)
	val chats = _chats.asStateFlow()
	
	// User function to create a new chat request
	fun createChatRequest(subject: String, message: String): String {
		val chatId = UUID.randomUUID().toString()
		
		// For UI display, create a ChatInfo
		val newChatInfo = ChatInfo(
			id = chatId,
			title = subject,
			isActive = false, // Pending, not active yet
			lastMessageText = message,
			lastMessageTime = System.currentTimeMillis(),
			unreadCount = 0,
			isRead = true
		)
		
		_chats.update { currentChats ->
			listOf(newChatInfo) + currentChats
		}
		
		// In a real implementation:
		// 1. Create a Chat with status=PENDING in Firestore
		// 2. Add current user as the only participant
		// 3. Add first message
		
		return chatId
	}
	
	// Will be replaced with Firebase implementation
	fun loadChats() {
		// Already loaded with mock data for now
		// Later: db.collection("chats").whereArrayContains("participantIds", currentUserId)
	}
	
	fun startNewChat(subject: String, message: String): String {
		val chatId = UUID.randomUUID().toString()
		
		val newChatInfo = ChatInfo(
			id = chatId,
			title = subject,
			isActive = false, // Starts as pending
			lastMessageText = message,
			lastMessageTime = System.currentTimeMillis(),
			unreadCount = 0,
			isRead = true
		)
		
		_chats.update { currentChats ->
			listOf(newChatInfo) + currentChats
		}
		
		// Later: Create in Firestore
		return chatId
	}
	
	// Mark a chat as read and reset unread count
	fun markChatAsRead(chatId: String) {
		_chats.update { currentChats ->
			currentChats.map { chat ->
				if (chat.id == chatId) chat.copy(isRead = true, unreadCount = 0) else chat
			}
		}
		
		// Later: Update participant data in Firestore
	}
	
	// Close a chat (user side)
	fun closeChat(chatId: String) {
		_chats.update { currentChats ->
			currentChats.map { chat ->
				if (chat.id == chatId) chat.copy(isActive = false) else chat
			}
		}
		
		// Later: Update chat status to CLOSED in Firestore
	}
	
	// Delete a chat from local view
	fun deleteChat(chatId: String) {
		_chats.update { currentChats ->
			currentChats.filter { it.id != chatId }
		}
		
		// Later: Mark chat as DELETED in Firestore
	}
}