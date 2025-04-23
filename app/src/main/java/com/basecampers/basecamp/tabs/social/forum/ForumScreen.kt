package com.basecampers.basecamp.tabs.social.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampSearchBar
import com.basecampers.basecamp.ui.theme.AppBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
	isPrivilegedUser: Boolean,
) {
	var searchQuery by remember { mutableStateOf("") }
	
	Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
		) {
			// Search and filter section
			BasecampSearchBar(
				query = searchQuery,
				onQueryChange = { searchQuery = it },
				placeholder = "Search in forum...",
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, bottom = 16.dp)
			)
			
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
		if (isPrivilegedUser) {
			FloatingActionButton(
				onClick = { /* TODO: Implement create post */ },
				modifier = Modifier
					.align(Alignment.BottomStart)
					.padding(16.dp)
			) {
				Icon(Icons.Default.Add, contentDescription = "Create Post")
			}
		}
	}
}