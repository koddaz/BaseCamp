package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel

@Composable
fun UserMessagingScreen(
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onStartNewChat: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	val chats by viewModel.chats.collectAsState()
	val activeChat = chats.firstOrNull { it.isActive }
	val closedChats = chats.filter { !it.isActive }
	
	Column(modifier = Modifier.fillMaxSize()) {
		Text(
			text = "User Messaging",
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(16.dp)
		)
		
		// Active Chat
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "Active Chat",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				if (activeChat != null) {
					// Show active chat
					Button(
						onClick = { onSelectActiveChat(activeChat.id) },
						modifier = Modifier.fillMaxWidth()
					) {
						Text("Continue chat with BaseBuddy")
					}
				} else {
					// Show start new chat button
					Button(
						onClick = onStartNewChat,
						modifier = Modifier.fillMaxWidth()
					) {
						Icon(Icons.Default.Add, contentDescription = "Start Chat")
						Spacer(modifier = Modifier.width(8.dp))
						Text("Start new chat with BaseBuddy")
					}
				}
			}
		}
		
		// Closed Chats
		Text(
			text = "Chat History",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp)
		)
		
		if (closedChats.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				contentAlignment = Alignment.Center
			) {
				Text("No chat history")
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = 16.dp)
			) {
				items(closedChats) { chat ->
					OutlinedCard(
						onClick = { onSelectClosedChat(chat.id) },
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 8.dp)
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(16.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "Chat from ${chat.lastMessageTime}",
								modifier = Modifier.weight(1f)
							)
							
							Text(
								text = "(Closed)",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.outline
							)
						}
					}
				}
			}
		}
	}
}