package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import com.example.basecamp.tabs.social.models.Chat
import com.example.basecamp.tabs.social.models.Message
import com.example.basecamp.tabs.social.models.User
import com.example.basecamp.tabs.social.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class UserMessagingViewModel : ViewModel() {
	// Mock current user
	private val currentUserId = "user_1"
	
	// Mock data for user's chats
	private val _chats = MutableStateFlow<List<UserChat>>(
		listOf(
			UserChat(
				id = "chat1",
				isActive = true,
				lastMessageTime = "10 minutes ago"
			),
			UserChat(
				id = "chat2",
				isActive = false,
				lastMessageTime = "Yesterday"
			),
			UserChat(
				id = "chat3",
				isActive = false,
				lastMessageTime = "2 days ago"
			)
		)
	)
	val chats: StateFlow<List<UserChat>> = _chats.asStateFlow()
	
	// Mock list of available super users
	private val _superUsers = MutableStateFlow<List<User>>(
		listOf(
			User(id = "bb_1", name = "Sarah (BaseBuddy)", role = UserRole.SUPER_USER),
			User(id = "bb_2", name = "Mike (BaseBuddy)", role = UserRole.SUPER_USER),
			User(id = "admin_1", name = "Admin", role = UserRole.ADMIN)
		)
	)
	
	// Get list of available super users
	fun getAvailableSuperUsers(): StateFlow<List<User>> {
		return _superUsers.asStateFlow()
	}
	
	// Start a new chat
	fun startNewChat(topic: String, initialMessage: String): String {
		// In a real app, this would create a new chat request and assign it to available BaseBuddies
		val chatId = UUID.randomUUID().toString()
		
		// Add to active chats (in a real app, it would be pending until accepted)
		val newChat = UserChat(
			id = chatId,
			isActive = true,
			lastMessageTime = "Just now"
		)
		
		_chats.value = listOf(newChat) + _chats.value.filter { !it.isActive }
		
		return chatId
	}
	
	// Mark chat as closed
	fun closeChat(chatId: String) {
		_chats.value = _chats.value.map {
			if (it.id == chatId) it.copy(isActive = false) else it
		}
	}
	
	// Data class for user chats
	data class UserChat(
		val id: String,
		val isActive: Boolean,
		val lastMessageTime: String
	)
}