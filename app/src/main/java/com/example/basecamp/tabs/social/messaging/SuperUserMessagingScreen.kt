package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel

@Composable
fun SuperUserMessagingScreen(
	onSelectPendingChat: (String) -> Unit,
	onSelectActiveChat: (String) -> Unit,
	viewModel: SuperUserMessagingViewModel = viewModel()
) {
	val pendingChats by viewModel.pendingChats.collectAsState()
	val activeChats by viewModel.activeChats.collectAsState()
	
	Column(modifier = Modifier.fillMaxSize()) {
		Text(
			text = "BaseBuddy Messaging Console",
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(16.dp)
		)
		
		// Pending Chat Requests
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "Pending Chat Requests",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				if (pendingChats.isEmpty()) {
					Text("No pending requests")
				} else {
					LazyColumn {
						items(pendingChats) { request ->
							OutlinedCard(
								onClick = { onSelectPendingChat(request.id) },
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 4.dp)
							) {
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(16.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Column(modifier = Modifier.weight(1f)) {
										Text(
											text = "Request from ${request.userName}",
											fontWeight = FontWeight.Bold
										)
										Text(
											text = "Subject: ${request.subject}",
											style = MaterialTheme.typography.bodyMedium
										)
									}
									
									Text(
										text = request.timeReceived,
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
		
		// Active Chats
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(
					text = "Active Chats",
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				if (activeChats.isEmpty()) {
					Text("No active chats")
				} else {
					LazyColumn {
						items(activeChats) { chat ->
							OutlinedCard(
								onClick = { onSelectActiveChat(chat.id) },
								modifier = Modifier
									.fillMaxWidth()
									.padding(vertical = 4.dp)
							) {
								Row(
									modifier = Modifier
										.fillMaxWidth()
										.padding(16.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Column(modifier = Modifier.weight(1f)) {
										Text(
											text = "Chat with ${chat.userName}",
											fontWeight = FontWeight.Bold
										)
										Text(
											text = "Last message: ${chat.lastMessageTime}",
											style = MaterialTheme.typography.bodySmall
										)
									}
								}
							}
						}
					}
				}
			}
		}
	}
}