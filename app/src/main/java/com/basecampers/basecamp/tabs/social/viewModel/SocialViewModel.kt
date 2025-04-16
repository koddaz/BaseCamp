package com.basecampers.basecamp.tabs.social.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for the Social tab section
 */
class SocialViewModel : ViewModel() {
	
	// Number of unread messages
	// In production, this would be loaded from a messaging repository
	private val _unreadCount = MutableStateFlow(3)
	val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
	
	// Update unread message count
	fun updateUnreadCount(count: Int) {
		_unreadCount.value = count
	}
	
	// Current user's super user status
	// In production, this would be loaded from a user session or repository
	private val _isSuper = MutableStateFlow(false)
	val isSuper: StateFlow<Boolean> = _isSuper.asStateFlow()
	
	// Show/hide menu state
	private val _showMenu = MutableStateFlow(false)
	val showMenu = _showMenu.asStateFlow()
	
	// Toggle super user status (for testing purposes only)
	fun toggleSuperUser() {
		_isSuper.value = !_isSuper.value
	}
	
	fun toggleMenu() {
		_showMenu.value = !_showMenu.value
	}
	
	fun hideMenu() {
		_showMenu.value = false
	}
}

