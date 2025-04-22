package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.HorizontalOptionCard
import com.basecampers.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel

@Composable
fun UserMessagingScreen(
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onStartNewChat: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	Column(modifier = Modifier.fillMaxSize()) {
		Text(
			text = "Messages",
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(16.dp)
		)
		
		// Start New Chat Button
		HorizontalOptionCard(
			title = "New Conversation",
			onClick = onStartNewChat,
			icon = Icons.Default.Add,
			iconBackground = MaterialTheme.colorScheme.primary
		)
		
		Spacer(modifier = Modifier.height(16.dp))
		
		// Active Chats
		Text(
			text = "Active Conversations",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp)
		)
		
		Spacer(modifier = Modifier.height(8.dp))
		
		// Placeholder active chat
		OutlinedCard(
			onClick = { onSelectActiveChat("active-chat-1") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(text = "Support Chat")
				Text(
					text = "Thanks for your help!",
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
		
		Spacer(modifier = Modifier.height(16.dp))
		
		// Chat History
		Text(
			text = "Chat History",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp)
		)
		
		Spacer(modifier = Modifier.height(8.dp))
		
		// Placeholder closed chat
		OutlinedCard(
			onClick = { onSelectClosedChat("closed-chat-1") },
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 4.dp)
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				Text(text = "Booking Issue")
				Text(
					text = "Issue has been resolved. (Closed)",
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}

@Composable
private fun EmptyStateMessage() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(32.dp),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Icon(
				imageVector = Icons.Default.Message,
				contentDescription = null,
				modifier = Modifier.size(48.dp),
				tint = MaterialTheme.colorScheme.outline
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = "No conversations yet",
				style = MaterialTheme.typography.bodyLarge,
				color = MaterialTheme.colorScheme.outline
			)
		}
	}
}