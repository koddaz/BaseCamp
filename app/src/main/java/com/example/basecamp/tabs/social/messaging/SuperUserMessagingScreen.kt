package com.example.basecamp.tabs.social.messaging

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.components.formatTime
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel

@Composable
fun SuperUserMessagingScreen(
	onSelectPendingChat: (String) -> Unit,
	onSelectActiveChat: (String) -> Unit
) {
	val context = LocalContext.current
	val viewModel: SuperUserMessagingViewModel = viewModel(
		factory = SuperUserMessagingViewModel.Factory(context)
	)
	
	val pendingChats by viewModel.getPendingChats().collectAsState(initial = emptyList())
	val activeChats by viewModel.getAssignedChats().collectAsState(initial = emptyList())
	var expandPendingSection by remember { mutableStateOf(false) }
	
	Column(modifier = Modifier.fillMaxSize()) {
		// Header
		Text(
			text = "BaseBuddy Message Center",
			style = MaterialTheme.typography.headlineSmall,
			modifier = Modifier.padding(16.dp)
		)
		
		// Pending Chats Section
		Card(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp, vertical = 8.dp)
		) {
			Column(modifier = Modifier.padding(16.dp)) {
				// Collapsible header
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clickable { expandPendingSection = !expandPendingSection },
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					Row(verticalAlignment = Alignment.CenterVertically) {
						Text(
							text = "Pending Requests",
							style = MaterialTheme.typography.titleMedium
						)
						
						if (pendingChats.isNotEmpty()) {
							Spacer(modifier = Modifier.width(8.dp))
							Badge { Text(text = pendingChats.size.toString()) }
						}
					}
					
					Icon(
						imageVector = if (expandPendingSection)
							Icons.Default.ExpandLess
						else
							Icons.Default.ExpandMore,
						contentDescription = if (expandPendingSection)
							"Collapse"
						else
							"Expand"
					)
				}
				
				// Expandable content
				AnimatedVisibility(visible = expandPendingSection) {
					if (pendingChats.isEmpty()) {
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 16.dp),
							contentAlignment = Alignment.Center
						) {
							Text("No pending requests")
						}
					} else {
						LazyColumn(
							modifier = Modifier
								.fillMaxWidth()
								.padding(top = 8.dp)
						) {
							items(pendingChats) { chat ->
								PendingChatItem(
									chat = chat,
									onClick = { onSelectPendingChat(chat.id) }
								)
								Divider()
							}
						}
					}
				}
			}
		}
		
		// Active Chats Section
		Text(
			text = "Active Conversations",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
		)
		
		if (activeChats.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.padding(32.dp),
				contentAlignment = Alignment.Center
			) {
				Text("No active conversations")
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f)
			) {
				items(activeChats) { chat ->
					ActiveChatItem(
						chat = chat,
						onClick = { onSelectActiveChat(chat.id) }
					)
					Divider()
				}
			}
		}
	}
}

@Composable
private fun PendingChatItem(
	chat: ChatInfo,
	onClick: () -> Unit
) {
	ListItem(
		headlineContent = { Text("Request from ${chat.title}") },
		supportingContent = { Text("Topic: ${chat.subject}") },
		trailingContent = {
			// Format timestamp to readable time
			val timestamp = formatTime(chat.lastMessageTime)
			Text(timestamp)
		},
		modifier = Modifier.clickable(onClick = onClick)
	)
}

@Composable
private fun ActiveChatItem(
	chat: ChatInfo,
	onClick: () -> Unit
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
				
				if (chat.unreadCount > 0) {
					Badge { Text(text = chat.unreadCount.toString()) }
				}
			}
		},
		modifier = Modifier.clickable(onClick = onClick)
	)
}