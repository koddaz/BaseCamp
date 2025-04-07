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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
	isSuper: Boolean,
	onToggleSuperUser: () -> Unit
) {
	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
		) {
			// Search and filter section
			OutlinedTextField(
				value = "",
				onValueChange = { /* TODO: Implement search */ },
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, bottom = 16.dp),
				placeholder = { Text("Search in forum...") },
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Search,
						contentDescription = "Search"
					)
				},
				singleLine = true
			)
			
			// Super user toggle (for testing purposes)
			if (isSuper) {
				TextButton(
					onClick = onToggleSuperUser,
					modifier = Modifier.align(Alignment.End)
				) {
					Text("Switch to User Mode")
				}
			} else {
				TextButton(
					onClick = onToggleSuperUser,
					modifier = Modifier.align(Alignment.End)
				) {
					Text("Switch to Super User Mode")
				}
			}
			
			// Placeholder forum content
			Box(
				modifier = Modifier
					.fillMaxSize()
					.weight(1f),
				contentAlignment = Alignment.Center
			) {
				Text("Forum Screen - To be implemented")
			}
		}
		
		// Add post button (only visible to Super Users)
		if (isSuper) {
			FloatingActionButton(
				onClick = { /* TODO: Implement create post */ },
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(16.dp)
			) {
				Icon(Icons.Default.Add, contentDescription = "Create Post")
			}
		}
	}
}