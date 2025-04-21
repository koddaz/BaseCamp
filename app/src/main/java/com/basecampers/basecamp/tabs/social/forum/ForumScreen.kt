package com.basecampers.basecamp.tabs.social.forum

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampSearchBar
import com.basecampers.basecamp.ui.theme.SecondaryAqua

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
	isSuper: Boolean,
) {
	var searchQuery by remember { mutableStateOf("") }
	
	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
		) {
			// Search and filter section
			BasecampSearchBar(
				query = searchQuery,
				onQueryChange = { searchQuery = it },
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = 16.dp, bottom = 16.dp),
				placeholder = "Search in forum..."
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
		if (isSuper) {
			FloatingActionButton(
				onClick = { /* TODO: Implement create post */ },
				modifier = Modifier
					.align(Alignment.BottomStart)
					.padding(16.dp),
				containerColor = SecondaryAqua,
				elevation = FloatingActionButtonDefaults.elevation(
					defaultElevation = 6.dp,
					pressedElevation = 12.dp,
					focusedElevation = 8.dp
				),
				content = {
					Row(
						modifier = Modifier.padding(horizontal = 16.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						Icon(
							imageVector = Icons.Default.Add,
							contentDescription = "Create new post",
							modifier = Modifier.size(24.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text(
							text = "New Post",
							style = MaterialTheme.typography.labelLarge
						)
					}
				}
			)
		}
	}
}