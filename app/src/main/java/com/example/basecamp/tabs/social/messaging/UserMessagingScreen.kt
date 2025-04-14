package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.components.formatTime
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel

@Composable
fun UserMessagingScreen(
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onStartNewChat: () -> Unit
) {
	val context = LocalContext.current
	val viewModel: UserMessagingViewModel = viewModel(
		factory = UserMessagingViewModel.Factory(context)
	)
	
	val activeChats by viewModel.getActiveChats().collectAsState(initial = emptyList())
	val closedChats by viewModel.getClosedChats().collectAsState(initial = emptyList())
	
	Column(modifier = Modifier.fillMaxSize()) {
		// Header with New Chat button
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = "Messages",
				style = MaterialTheme.typography.headlineSmall
			)
			
			Button(onClick = onStartNewChat) {
				Icon(Icons.Default.Add, contentDescription = "New Chat")
				Spacer(modifier = Modifier.width(4.dp))
				Text("New")
			}
		}
		
		// Active Chats Section
		if (activeChats.isNotEmpty()) {
			Text(
				text = "Active Conversations",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			
			activeChats.forEach { chat ->
				ChatInfoItem(
					chat = chat,
					onClick = { onSelectActiveChat(chat.id) }
				)
				Divider()
			}
		}
		
		// Closed Chats Section
		if (closedChats.isNotEmpty()) {
			Text(
				text = "Chat History",
				style = MaterialTheme.typography.titleMedium,
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			)
			
			closedChats.forEach { chat ->
				ChatInfoItem(
					chat = chat,
					onClick = { onSelectClosedChat(chat.id) },
					isClosed = true
				)
				Divider()
			}
		}
		
		// Empty state if no chats
		if (activeChats.isEmpty() && closedChats.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				contentAlignment = Alignment.Center
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Icon(
						imageVector = Icons.Default.ChatBubbleOutline,
						contentDescription = null,
						modifier = Modifier.size(64.dp),
						tint = MaterialTheme.colorScheme.outline
					)
					
					Spacer(modifier = Modifier.height(16.dp))
					
					Text(
						text = "No conversations yet",
						style = MaterialTheme.typography.bodyLarge,
						color = MaterialTheme.colorScheme.outline
					)
					
					Spacer(modifier = Modifier.height(8.dp))
					
					TextButton(onClick = onStartNewChat) {
						Text("Start a conversation")
					}
				}
			}
		}
	}
}

@Composable
private fun ChatInfoItem(
	chat: ChatInfo,
	onClick: () -> Unit,
	isClosed: Boolean = false
) {
	ListItem(
		headlineContent = { Text(chat.title) },
		supportingContent = { Text(chat.lastMessageText) },
		trailingContent = {
			Column(horizontalAlignment = Alignment.End) {
				Text(
					text = formatTime(chat.lastMessageTime),
					style = MaterialTheme.typography.bodySmall
				)
				
				if (chat.unreadCount > 0 && !isClosed) {
					Badge { Text(text = chat.unreadCount.toString()) }
				}
				
				if (isClosed) {
					Text(
						text = "(Closed)",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.outline
					)
				}
			}
		},
		modifier = Modifier.clickable(onClick = onClick)
	)
}