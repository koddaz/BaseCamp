package com.basecampers.basecamp.tabs.social.navHost

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SocialMenu(
	selectedTab: SocialTab,
	unreadCount: Int,
	isSuper: Boolean,
	onTabSelected: (SocialTab) -> Unit,
	onToggleSuperUser: () -> Unit
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(8.dp),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			// Messages Tab
			TabButton(
				text = "Messages",
				isSelected = selectedTab == SocialTab.MESSAGES,
				badge = if (unreadCount > 0) unreadCount.toString() else null,
				onClick = { onTabSelected(SocialTab.MESSAGES) }
			)
			
			Spacer(modifier = Modifier.width(8.dp))
			
			// QnA Tab
			TabButton(
				text = "QnA",
				isSelected = selectedTab == SocialTab.QNA,
				onClick = { onTabSelected(SocialTab.QNA) }
			)
			
			Spacer(modifier = Modifier.width(8.dp))
			
			// Forum Tab
			TabButton(
				text = "Forum",
				isSelected = selectedTab == SocialTab.FORUM,
				onClick = { onTabSelected(SocialTab.FORUM) }
			)
		}
		
		// Super User Toggle
		Switch(
			checked = isSuper,
			onCheckedChange = { onToggleSuperUser() },
			thumbContent = {
				Icon(
					imageVector = if (isSuper) Icons.Filled.AdminPanelSettings else Icons.Filled.Person,
					contentDescription = if (isSuper) "BaseBuddy Mode" else "User Mode",
					modifier = Modifier.size(16.dp)
				)
			}
		)
	}
}

@Composable
fun TabButton(
	text: String,
	isSelected: Boolean,
	badge: String? = null,
	onClick: () -> Unit
) {
	Button(
		onClick = onClick,
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isSelected)
				MaterialTheme.colorScheme.primary
			else
				MaterialTheme.colorScheme.surfaceVariant,
			contentColor = if (isSelected)
				MaterialTheme.colorScheme.onPrimary
			else
				MaterialTheme.colorScheme.onSurfaceVariant
		)
	) {
		if (badge != null) {
			BadgedBox(
				badge = {
					Badge {
						Text(badge)
					}
				}
			) {
				Text(text)
			}
		} else {
			Text(text)
		}
	}
}

enum class SocialTab {
	MESSAGES,
	QNA,
	FORUM
}