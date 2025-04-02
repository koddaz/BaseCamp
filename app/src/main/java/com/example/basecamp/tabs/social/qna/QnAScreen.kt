package com.example.basecamp.tabs.social.qna

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.basecamp.tabs.social.navHost.SocialMenu
import com.example.basecamp.tabs.social.navHost.SocialTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAScreen(
	onNavigateToQnA: () -> Unit,
	onNavigateToForum: () -> Unit,
	onNavigateToMessages: () -> Unit,
	unreadCount: Int,
	isSuper: Boolean,
	onToggleSuperUser: () -> Unit
) {
	Scaffold(
		topBar = {
			SocialMenu(
				selectedTab = SocialTab.QNA,
				unreadCount = unreadCount,
				isSuper = isSuper,
				onTabSelected = { tab ->
					when (tab) {
						SocialTab.QNA -> onNavigateToQnA()
						SocialTab.FORUM -> onNavigateToForum()
						SocialTab.MESSAGES -> onNavigateToMessages()
					}
				},
				onToggleSuperUser = onToggleSuperUser
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			// Search bar
			OutlinedTextField(
				value = "",
				onValueChange = { /* TODO: Implement search */ },
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				placeholder = { Text("Search questions...") },
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Search,
						contentDescription = "Search"
					)
				},
				singleLine = true
			)
			
			// Placeholder QnA content
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Text("Q&A Screen - To be implemented")
			}
		}
	}
}