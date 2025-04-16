package com.basecampers.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.social.messaging.models.Chat
import com.basecampers.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserMessagingScreen(
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onStartNewChat: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	val chats by viewModel.chats.collectAsState()
	val activeChats = chats.filter { it.isActive }
	val closedChats = chats.filter { !it.isActive }
	
	Column(modifier = Modifier.fillMaxSize()) {
		Text(
			text = "Messages",
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(16.dp)
		)
		
		// Start New Chat Button
		Button(
			onClick = onStartNewChat,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
		) {
			Icon(Icons.Default.Add, contentDescription = "Start Chat")
			Spacer(modifier = Modifier.width(8.dp))
			Text("New conversation")
		}
		
		Spacer(modifier = Modifier.height(16.dp))
		
		// Active Chats Section
		if (activeChats.isNotEmpty()) {
			Text(
				text = "Active Conversations",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			activeChats.forEach { chat ->
				ChatListItem(
					chat = chat,
					onClick = { onSelectActiveChat(chat.id) }
				)
			}
			
			Spacer(modifier = Modifier.height(16.dp))
		}
		
		// Chat History
		if (closedChats.isNotEmpty()) {
			Text(
				text = "Chat History",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(horizontal = 16.dp)
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			closedChats.forEach { chat ->
				ChatListItem(
					chat = chat,
					onClick = { onSelectClosedChat(chat.id) },
					isClosed = true
				)
			}
		} else if (activeChats.isEmpty()) {
			// Show empty state if no chats at all
			EmptyStateMessage()
		}
	}
}

@Composable
fun ChatListItem(
	chat: Chat,
	onClick: () -> Unit,
	isClosed: Boolean = false
) {
	OutlinedCard(
		onClick = onClick,
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 4.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(modifier = Modifier.weight(1f)) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = chat.title,
						style = MaterialTheme.typography.bodyLarge,
						fontWeight = FontWeight.Medium
					)
					
					if (chat.unreadCount > 0 && !isClosed) {
						Spacer(modifier = Modifier.width(8.dp))
						Badge { Text(text = chat.unreadCount.toString()) }
					}
				}
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Text(
					text = chat.lastMessageText,
					style = MaterialTheme.typography.bodyMedium,
					maxLines = 1
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Row {
					Text(
						text = formatTimestamp(chat.lastMessageTime),
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.outline
					)
					
					if (isClosed) {
						Spacer(modifier = Modifier.width(8.dp))
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

private fun formatTimestamp(timestamp: Long): String {
	val now = System.currentTimeMillis()
	val diff = now - timestamp
	
	return when {
		diff < 60 * 60 * 1000 -> "Just now"
		diff < 24 * 60 * 60 * 1000 -> "Today"
		diff < 48 * 60 * 60 * 1000 -> "Yesterday"
		else -> {
			val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
			sdf.format(Date(timestamp))
		}
	}
}