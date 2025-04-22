package com.basecampers.basecamp.tabs.social.navHost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel
import com.basecampers.basecamp.tabs.social.forum.ForumScreen
import com.basecampers.basecamp.tabs.social.messaging.navHost.MessagingNavHost
import com.basecampers.basecamp.tabs.social.qna.QnAScreen

@Composable
fun SocialNavHost(
	socialViewModel: SocialViewModel,
	selectedSocialTabIndex: Int,
	onSocialTabSelected: (Int) -> Unit = {}
) {
	val companyProfile by UserSession.companyProfile.collectAsState()
	val unreadCount by socialViewModel.unreadCount.collectAsState()
	val showMenu by socialViewModel.showMenu.collectAsState()
	
	val isPrivilegedUser = remember(companyProfile) {
		companyProfile?.status == UserStatus.ADMIN ||
				companyProfile?.status == UserStatus.SUPER_USER
	}
	
	var currentSocialTabIndex by remember { mutableIntStateOf(selectedSocialTabIndex) }
	
	LaunchedEffect(selectedSocialTabIndex) {
		currentSocialTabIndex = selectedSocialTabIndex
	}
	
	LaunchedEffect(currentSocialTabIndex) {
		socialViewModel.hideMenu()
	}
	
	Box(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxSize()) {
			
			// Instead of safearea... /Top bar space
			Spacer(modifier = Modifier.height(25.dp))
			
			if (companyProfile != null) {
				Text(
					text = "Role: ${companyProfile?.status}",
					modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
				)
			}
			Box(modifier = Modifier.weight(1f)) {
				when (currentSocialTabIndex) {
					0 -> QnAScreen(
						isPrivilegedUser = isPrivilegedUser
					)
					1 -> ForumScreen(
						isPrivilegedUser = isPrivilegedUser
					)
					2 -> MessagingNavHost(
						socialViewModel = socialViewModel,
						isPrivilegedUser = isPrivilegedUser,
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