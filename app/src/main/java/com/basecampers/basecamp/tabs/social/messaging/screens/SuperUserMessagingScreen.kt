package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel

@Composable
fun SuperUserMessagingScreen(
	onSelectPendingChat: (String) -> Unit,
	onSelectActiveChat: (String) -> Unit,
	viewModel: SuperUserMessagingViewModel = viewModel()
) {
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
					style = MaterialTheme.typography.titleMedium
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				// Simple placeholder pending chat
				OutlinedCard(
					onClick = { onSelectPendingChat("pending-chat-1") },
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
							Text(text = "Request from Taylor")
							Text(
								text = "App Functionality",
								style = MaterialTheme.typography.bodyMedium
							)
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
					style = MaterialTheme.typography.titleMedium
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				// Simple placeholder active chat
				OutlinedCard(
					onClick = { onSelectActiveChat("active-chat-1") },
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
							Text(text = "Chat with Alex")
							Text(
								text = "Last message: Just now",
								style = MaterialTheme.typography.bodySmall
							)
						}
					}
				}
			}
		}
	}
}