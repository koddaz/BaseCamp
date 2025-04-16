package com.basecampers.basecamp.tabs.social.navHost

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun SocialMenu(
	selectedTabIndex: Int,
	unreadCount: Int,
	showMenu: Boolean,
	onTabSelected: (Int) -> Unit,
	onToggleMenu: () -> Unit
) {
	Box(modifier = Modifier.fillMaxSize()) {
		// Arc Menu Items (only shown when menu is expanded)
		if (showMenu) {
			ArcMenuItems(
				selectedTabIndex = selectedTabIndex,
				unreadCount = unreadCount,
				onTabSelected = onTabSelected
			)
		}
		
		// FAB with rotation animation
		FloatingActionButton(
			onClick = onToggleMenu,
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.padding(16.dp)
		) {
			// Animate rotation between + and ×
			val rotation by animateFloatAsState(
				targetValue = if (showMenu) 45f else 0f,
				animationSpec = tween(durationMillis = 300)
			)
			
			Icon(
				imageVector = Icons.Filled.Add,
				contentDescription = if (showMenu) "Close Menu" else "Open Menu",
				modifier = Modifier.rotate(rotation)
			)
		}
	}
}

@Composable
private fun ArcMenuItems(
	selectedTabIndex: Int,
	unreadCount: Int,
	onTabSelected: (Int) -> Unit
) {
	// Define menu items
	val menuItems = listOf(
		SocialTabItem("Q&A", Icons.Filled.QuestionMark, Icons.Outlined.QuestionMark, 0),
		SocialTabItem("Forum", Icons.Filled.Notes, Icons.Outlined.Notes, 1),
		SocialTabItem(
			"Messages",
			Icons.Filled.Send,
			Icons.Outlined.Send,
			2,
			if (unreadCount > 0) unreadCount else null
		)
	)
	
	// Get screen dimensions using LocalConfiguration
	val configuration = LocalConfiguration.current
	val screenWidth = configuration.screenWidthDp
	val screenHeight = configuration.screenHeightDp
	
	// Calculate spacing based on screen size
	val spacing = (screenWidth.coerceAtMost(screenHeight) * 0.20f).dp
	
	// The positions (relative to bottom-right corner)
	val positions = listOf(
		Offset(x = -spacing.value, y = 0f),           // Left (Q&A)
		Offset(x = -spacing.value * 0.707f, y = -spacing.value * 0.707f),  // Top-left diagonal (Forum)
		Offset(x = 0f, y = -spacing.value)            // Top (Messages)
	)
	
	Box(
		modifier = Modifier
			.fillMaxSize()
			.padding(bottom = 20.dp, end = 20.dp)
	) {
		// Position each menu item with adaptive spacing
		menuItems.forEachIndexed { index, item ->
			val position = positions[index]
			
			// Staggered animation for appearing
			val scale by animateFloatAsState(
				targetValue = 1f,
				animationSpec = spring(
					dampingRatio = Spring.DampingRatioMediumBouncy,
					stiffness = Spring.StiffnessLow
				),
				label = "Menu item scale"
			)
			
			Box(
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.offset(x = position.x.dp, y = position.y.dp)
					.scale(scale)
			) {
				ArcMenuItem(
					item = item,
					isSelected = selectedTabIndex == item.tabIndex,
					onClick = { onTabSelected(item.tabIndex) }
				)
			}
		}
	}
}

@Composable
private fun ArcMenuItem(
	item: SocialTabItem,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	FloatingActionButton(
		onClick = onClick,
		containerColor = if (isSelected)
			MaterialTheme.colorScheme.primary
		else
			MaterialTheme.colorScheme.surfaceVariant,
		contentColor = if (isSelected)
			MaterialTheme.colorScheme.onPrimary
		else
			MaterialTheme.colorScheme.onSurfaceVariant
	) {
		BadgedBox(
			badge = {
				if (item.badgeCount != null) {
					Badge { Text(item.badgeCount.toString()) }
				}
			}
		) {
			Icon(
				imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
				contentDescription = item.label
			)
		}
	}
}

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