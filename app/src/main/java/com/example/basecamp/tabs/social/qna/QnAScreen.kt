package com.example.basecamp.tabs.social.qna

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAScreen(
	isSuper: Boolean,
	onToggleSuperUser: () -> Unit
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = 16.dp)
	) {
		// Search bar
		OutlinedTextField(
			value = "",
			onValueChange = { /* TODO: Implement search */ },
			modifier = Modifier
				.fillMaxWidth()
				.padding(top = 16.dp, bottom = 16.dp),
			placeholder = { Text("Search questions...") },
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
		
		// Placeholder QnA content
		Box(
			modifier = Modifier
				.fillMaxSize()
				.weight(1f),
			contentAlignment = Alignment.Center
		) {
			Text("Q&A Screen - To be implemented")
		}
	}
}