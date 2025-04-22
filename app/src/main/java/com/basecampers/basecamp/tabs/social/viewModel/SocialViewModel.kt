package com.basecampers.basecamp.tabs.social.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared ViewModel for the Social tab section
 */
class SocialViewModel : ViewModel() {
	
	//=== MESSAGING SECTION ===//
	
	// Number of unread messages
	private val _unreadCount = MutableStateFlow(3)
	val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
	
	// Update unread message count
	fun updateUnreadCount(count: Int) {
		_unreadCount.value = count
	}
	
	//=== NAVIGATION SECTION ===//
	
	// Show/hide menu state
	private val _showMenu = MutableStateFlow(false)
	val showMenu = _showMenu.asStateFlow()
	
	fun toggleMenu() {
		_showMenu.value = !_showMenu.value
	}
	
	fun hideMenu() {
		_showMenu.value = false
	}
}