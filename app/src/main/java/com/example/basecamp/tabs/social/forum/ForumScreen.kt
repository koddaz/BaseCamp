package com.example.basecamp.tabs.social.forum

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.basecamp.tabs.social.navHost.SocialMenu
import com.example.basecamp.tabs.social.navHost.SocialTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
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
				selectedTab = SocialTab.FORUM,
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
		},
		floatingActionButton = {
			FloatingActionButton(onClick = { /* TODO: Implement create post */ }) {
				Icon(Icons.Default.Add, contentDescription = "Create Post")
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			// Search and filter section
			OutlinedTextField(
				value = "",
				onValueChange = { /* TODO: Implement search */ },
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				placeholder = { Text("Search in forum...") },
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Search,
						contentDescription = "Search"
					)
				},
				singleLine = true
			)
			
			// Placeholder forum content
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				Text("Forum Screen - To be implemented")
			}
		}
	}
}