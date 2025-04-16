package com.basecampers.basecamp.tabs.social.navHost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel
import com.basecampers.basecamp.tabs.social.forum.ForumScreen
import com.basecampers.basecamp.tabs.social.messaging.navHost.MessagingNavHost
import com.basecampers.basecamp.tabs.social.qna.QnAScreen

@Composable
fun SocialNavHost(
	authViewModel: AuthViewModel,
	socialViewModel: SocialViewModel,
	selectedSocialTabIndex: Int = 1,
	onSocialTabSelected: (Int) -> Unit = {}
) {
	// Get states from ViewModel
	val isSuper by authViewModel.isSuper.collectAsState()
	val unreadCount by socialViewModel.unreadCount.collectAsState()
	val showMenu by socialViewModel.showMenu.collectAsState()
	// Local copy of the selectedSocialTabIndex
	var currentSocialTabIndex by remember { mutableIntStateOf(selectedSocialTabIndex) }
	
	// Update current tab when external state changes
	LaunchedEffect(selectedSocialTabIndex) {
		currentSocialTabIndex = selectedSocialTabIndex
	}
	
	// When tab changes, hide menu
	LaunchedEffect(currentSocialTabIndex) {
		socialViewModel.hideMenu()
	}
	
	
	Box(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxSize()) {
			// Add top padding for safe area (temporary solution)
			Spacer(modifier = Modifier.height(25.dp))
			
			// SuperUser toggle (discreetly placed at the top)
			Row(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				TextButton(
					onClick = { socialViewModel.toggleSuperUser() }
				) {
					Text(if (isSuper) "SuperUser Mode" else "User Mode")
				}
				Spacer(modifier = Modifier.weight(1f))
				Switch(
					checked = isSuper,
					onCheckedChange = { socialViewModel.toggleSuperUser() }
				)
			}
			
			// Content area - takes full remaining space
			Box(modifier = Modifier.weight(1f)) {
				when (currentSocialTabIndex) {
					0 -> QnAScreen(
						isSuper = isSuper,
						onToggleSuperUser = { socialViewModel.toggleSuperUser() }
					)
					1 -> ForumScreen(
						isSuper = isSuper,
						onToggleSuperUser = { socialViewModel.toggleSuperUser() }
					)
					2 -> MessagingNavHost(
						socialViewModel = socialViewModel,
						isSuper = isSuper,
						unreadCount = unreadCount
					)
					else -> Text("Error: Social tab not found")
				}
			}
		}
		
		// Social Menu Overlay
		SocialMenu(
			selectedTabIndex = currentSocialTabIndex,
			unreadCount = unreadCount,
			showMenu = showMenu,
			onTabSelected = {
				currentSocialTabIndex = it
				onSocialTabSelected(it)
			},
			onToggleMenu = { socialViewModel.toggleMenu() }
		)
	}
}