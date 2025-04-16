package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.social.messaging.MessageItem
import com.basecampers.basecamp.tabs.social.messaging.models.Message
import com.basecampers.basecamp.tabs.social.messaging.viewModels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
	chatId: String,
	isReadOnly: Boolean,
	onNavigateBack: () -> Unit,
	viewModel: ChatViewModel = viewModel()
) {
	var messageText by remember { mutableStateOf("") }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Chat") },
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
		) {
			// Simple placeholder message
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.padding(16.dp),
				contentAlignment = Alignment.Center
			) {
				MessageItem(
					message = Message(
						content = "Welcome to the chat! This is a placeholder message.",
						senderName = "System"
					),
					isFromCurrentUser = false
				)
			}
			
			// Input field
			if (!isReadOnly) {
				Card(
					modifier = Modifier.fillMaxWidth(),
					elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
				) {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(8.dp),
						verticalAlignment = Alignment.CenterVertically
					) {
						OutlinedTextField(
							value = messageText,
							onValueChange = { messageText = it },
							modifier = Modifier.weight(1f),
							placeholder = { Text("Type a message...") },
							maxLines = 3
						)
						
						Spacer(modifier = Modifier.width(8.dp))
						
						IconButton(onClick = {
							if (messageText.isNotBlank()) {
								messageText = ""
							}
						}) {
							Icon(
								imageVector = Icons.Default.Send,
								contentDescription = "Send"
							)
						}
					}
				}
			} else {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					contentAlignment = Alignment.Center
				) {
					Text("This chat is read-only")
				}
			}
		}
	}
}