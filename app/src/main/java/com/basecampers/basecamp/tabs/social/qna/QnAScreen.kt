package com.basecampers.basecamp.tabs.social.qna

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
fun QnAScreen(
	isSuper: Boolean,
) {
	Box(modifier = Modifier.fillMaxSize()) {
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
		if (isSuper) {
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