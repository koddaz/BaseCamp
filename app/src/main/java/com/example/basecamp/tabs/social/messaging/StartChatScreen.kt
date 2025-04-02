package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
	onChatStarted: (String) -> Unit,
	onNavigateBack: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	val superUsers by viewModel.getAvailableSuperUsers().collectAsState(initial = emptyList())
	var selectedTopic by remember { mutableStateOf("") }
	var messageText by remember { mutableStateOf("") }
	
	val topics = listOf("Booking Issue", "App Functionality", "Facility Maintenance", "Error Report")
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Start a New Chat") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				}
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
				.padding(16.dp)
		) {
			Text(
				text = "Start a conversation with a BaseBuddy",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = "Select a topic:",
				style = MaterialTheme.typography.bodyLarge
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			// Topic selection chips
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp)
			) {
				topics.forEach { topic ->
					FilterChip(
						selected = topic == selectedTopic,
						onClick = { selectedTopic = topic },
						label = { Text(topic) }
					)
				}
			}
			
			Spacer(modifier = Modifier.height(16.dp))
			
			OutlinedTextField(
				value = messageText,
				onValueChange = { messageText = it },
				modifier = Modifier
					.fillMaxWidth()
					.height(120.dp),
				label = { Text("Message") },
				placeholder = { Text("Describe what you need help with...") }
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Button(
				onClick = {
					if (selectedTopic.isNotBlank() && messageText.isNotBlank()) {
						val chatId = viewModel.startNewChat(selectedTopic, messageText)
						onChatStarted(chatId)
					}
				},
				enabled = selectedTopic.isNotBlank() && messageText.isNotBlank(),
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Submit Chat Request")
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Available BaseBuddies (just informational, not for selection)
			if (superUsers.isNotEmpty()) {
				Text(
					text = "Available BaseBuddies",
					style = MaterialTheme.typography.titleSmall
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				LazyColumn {
					items(superUsers) { superUser ->
						ListItem(
							headlineContent = { Text(superUser.name) },
							supportingContent = {
								Text("${superUser.role} • Active Now")
							}
						)
						Divider()
					}
				}
			}
		}
	}
}