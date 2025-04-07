package com.example.basecamp.tabs.social

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for the Social tab section
 */
class SocialViewModel : ViewModel() {
	// Current user's super user status
	// In production, this would be loaded from a user session or repository
	private val _isSuper = MutableStateFlow(false)
	val isSuper: StateFlow<Boolean> = _isSuper.asStateFlow()
	
	// Number of unread messages
	// In production, this would be loaded from a messaging repository
	private val _unreadCount = MutableStateFlow(3)
	val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
	
	// Toggle super user status (for testing purposes only)
	fun toggleSuperUser() {
		_isSuper.value = !_isSuper.value
	}
	
	// Update unread message count
	fun updateUnreadCount(count: Int) {
		_unreadCount.value = count
	}
}

//class SocialViewModel : ViewModel() {
//	// Mock current user
//	private val currentUserId = "user_1"
//
//	// Mock unread messages count
//	private val _unreadMessages = MutableStateFlow(3)
//	val unreadMessages: StateFlow<Int> = _unreadMessages.asStateFlow()
//
//	// Functions to manage unread messages
//	fun incrementUnreadMessages() {
//		_unreadMessages.value = _unreadMessages.value + 1
//	}
//
//	fun decrementUnreadMessages() {
//		if (_unreadMessages.value > 0) {
//			_unreadMessages.value = _unreadMessages.value - 1
//		}
//	}
//
//	fun resetUnreadMessages() {
//		_unreadMessages.value = 0
//	}
//}