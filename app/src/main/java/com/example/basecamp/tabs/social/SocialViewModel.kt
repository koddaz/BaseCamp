package com.example.basecamp.tabs.social

import androidx.lifecycle.ViewModel
import com.example.basecamp.tabs.social.models.Chat
import com.example.basecamp.tabs.social.models.Message
import com.example.basecamp.tabs.social.models.User
import com.example.basecamp.tabs.social.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SocialViewModel : ViewModel() {
	// Mock current user
	private val currentUserId = "user_1"
	
	// Mock unread messages count
	private val _unreadMessages = MutableStateFlow(3)
	val unreadMessages: StateFlow<Int> = _unreadMessages.asStateFlow()
	
	// Functions to manage unread messages
	fun incrementUnreadMessages() {
		_unreadMessages.value = _unreadMessages.value + 1
	}
	
	fun decrementUnreadMessages() {
		if (_unreadMessages.value > 0) {
			_unreadMessages.value = _unreadMessages.value - 1
		}
	}
	
	fun resetUnreadMessages() {
		_unreadMessages.value = 0
	}
}