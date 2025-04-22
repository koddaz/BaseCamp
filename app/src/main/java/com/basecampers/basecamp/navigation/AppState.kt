package com.basecampers.basecamp.navigation

/**
 * Singleton object to store persistent tab state across the app
 */
object AppState {
	var selectedMainTabIndex = 0 // Default to HomeNavHost
	var selectedSocialTabIndex = 1 // Default to Forum
	var selectedProfileTabIndex = 0 // Default to main profile
}