package com.basecampers.basecamp.tabs.social.navHost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
	// lets take the states from UserSession insataed where it makes sense, isSuper should be
	// added.
	val isSuper by authViewModel.isSuper.collectAsState()
	val unreadCount by socialViewModel.unreadCount.collectAsState()
	val showMenu by socialViewModel.showMenu.collectAsState()
	
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
			
			// Instead of safearea... /Top bar space
			Spacer(modifier = Modifier.height(25.dp))
			
			// ONLY WHILE TESTING
			Row(
				modifier = Modifier
					.padding(horizontal = 16.dp)
					.padding(bottom = 8.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Switch(
					checked = isSuper,
					onCheckedChange = { authViewModel.toggleSuperUser()}
				)
				Spacer(modifier = Modifier.width(8.dp))
				if (isSuper) {
					Text("SuperUser")
				} else {
					Text("User")
				}
			}
			// ONLY WHILE TESTING
			
			
			Box(modifier = Modifier.weight(1f)) {
				when (currentSocialTabIndex) {
					0 -> QnAScreen(
						isSuper = isSuper
					)
					1 -> ForumScreen(
						isSuper = isSuper
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