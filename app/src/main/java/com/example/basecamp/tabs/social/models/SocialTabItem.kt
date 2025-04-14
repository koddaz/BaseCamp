package com.example.basecamp.tabs.social.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an item in the social navigation menu
 */
data class SocialTabItem(
	val label: String,
	val selectedIcon: ImageVector,
	val unselectedIcon: ImageVector,
	val tabIndex: Int,
	val badgeCount: Int? = null
)