package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.models.Chat
import com.example.basecamp.tabs.social.messaging.models.ChatRequest
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
	isSuper: Boolean,
	onNavigateBack: () -> Unit,
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onSelectChatRequest: (String) -> Unit,
	onStartNewChat: () -> Unit,
	userViewModel: UserMessagingViewModel = viewModel(),
	superViewModel: SuperUserMessagingViewModel = viewModel()
) {
	var showClosedChats by remember { mutableStateOf(false) }
	var selectedTab by remember { mutableStateOf(0) }
	
	val tabs = if (isSuper) {
		listOf("Active Chats", "Chat Requests")
	} else {
		listOf("My Chats")
	}
	
	// Get data from appropriate ViewModel based on user role
	val chats by if (isSuper) {
		superViewModel.activeChats.collectAsState()
	} else {
		userViewModel.chats.collectAsState()
	}
	
	val chatRequests by if (isSuper) {
		superViewModel.pendingChats.collectAsState()
	} else {
		remember { mutableStateOf(emptyList<ChatRequest>()) }
	}
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Messages") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				actions = {
					// Only for BaseBuddy/Admin view
					if (isSuper) {
						IconButton(onClick = { showClosedChats = !showClosedChats }) {
							Icon(
								imageVector = if (showClosedChats) Icons.Default.VisibilityOff else Icons.Default.Visibility,
								contentDescription = if (showClosedChats) "Hide closed chats" else "Show closed chats"
							)
						}
					}
				}
			)
		},
		floatingActionButton = {
			if (!isSuper || selectedTab == 0) { // Show FAB for users always, or for superUsers only in active chats tab
				FloatingActionButton(
					onClick = onStartNewChat
				) {
					Icon(Icons.Default.Add, contentDescription = "New Chat Request")
				}
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			// Tabs - only show tabs for SuperUsers
			if (isSuper) {
				TabRow(selectedTabIndex = selectedTab) {
					tabs.forEachIndexed { index, title ->
						Tab(
							selected = selectedTab == index,
							onClick = { selectedTab = index },
							text = { Text(title) }
						)
					}
				}
			}
			
			// Content
			when {
				// SuperUser - Active Chats Tab
				isSuper && selectedTab == 0 -> {
					val filteredChats = if (showClosedChats) {
						chats
					} else {
						chats.filter { it.isActive }
					}
					
					if (filteredChats.isEmpty()) {
						EmptyState(message = "No active chats")
					} else {
						ChatList(
							chats = filteredChats,
							onChatSelected = { chat ->
								if (chat.isActive) {
									onSelectActiveChat(chat.id)
								} else {
									onSelectClosedChat(chat.id)
								}
							}
						)
					}
				}
				
				// SuperUser - Chat Requests Tab
				isSuper && selectedTab == 1 -> {
					if (chatRequests.isEmpty()) {
						EmptyState(message = "No pending chat requests")
					} else {
						ChatRequestList(
							requests = chatRequests,
							onAccept = { requestId ->
								superViewModel.acceptChatRequest(requestId)
								onSelectChatRequest(requestId)
							},
							onDecline = { requestId ->
								superViewModel.declineChatRequest(requestId)
							}
						)
					}
				}
				
				// Regular User - My Chats
				else -> {
					val activeChats = chats.filter { it.isActive }
					val closedChats = chats.filter { !it.isActive }
					
					if (chats.isEmpty()) {
						EmptyState(message = "No chats yet")
					} else {
						Column {
							if (activeChats.isNotEmpty()) {
								Text(
									text = "Active Chats",
									style = MaterialTheme.typography.titleMedium,
									modifier = Modifier.padding(16.dp)
								)
								
								ChatList(
									chats = activeChats,
									onChatSelected = { chat -> onSelectActiveChat(chat.id) }
								)
							}
							
							if (closedChats.isNotEmpty()) {
								Text(
									text = "Chat History",
									style = MaterialTheme.typography.titleMedium,
									modifier = Modifier.padding(16.dp)
								)
								
								ChatList(
									chats = closedChats,
									onChatSelected = { chat -> onSelectClosedChat(chat.id) }
								)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
fun EmptyState(message: String) {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		Text(
			text = message,
			style = MaterialTheme.typography.bodyLarge
		)
	}
}

@Composable
fun ChatList(
	chats: List<Chat>,
	onChatSelected: (Chat) -> Unit
) {
	LazyColumn {
		items(chats) { chat ->
			ChatItem(
				chat = chat,
				onClick = { onChatSelected(chat) }
			)
			Divider()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatItem(
	chat: Chat,
	onClick: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		onClick = onClick,
		colors = CardDefaults.cardColors(
			containerColor = if (chat.unreadCount > 0)
				MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
			else
				MaterialTheme.colorScheme.surface
		)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			// Avatar
			Box(
				modifier = Modifier
					.size(48.dp)
					.clip(CircleShape)
					.background(MaterialTheme.colorScheme.primaryContainer),
				contentAlignment = Alignment.Center
			) {
				Text(
					text = chat.title.firstOrNull()?.toString() ?: "C",
					style = MaterialTheme.typography.titleMedium,
					color = MaterialTheme.colorScheme.onPrimaryContainer
				)
			}
			
			Spacer(modifier = Modifier.width(16.dp))
			
			Column(modifier = Modifier.weight(1f)) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = chat.title,
						style = MaterialTheme.typography.titleMedium,
						fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
					)
					
					Spacer(modifier = Modifier.width(8.dp))
					
					if (!chat.isActive) {
						Text(
							text = "(Closed)",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.outline
						)
					}
				}
				
				Spacer(modifier = Modifier.height(4.dp))
				
				Text(
					text = chat.lastMessageText,
					style = MaterialTheme.typography.bodyMedium,
					color = if (chat.unreadCount > 0)
						MaterialTheme.colorScheme.onSurface
					else
						MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2
				)
			}
			
			Column(
				horizontalAlignment = Alignment.End
			) {
				Text(
					text = formatTime(chat.lastMessageTime),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.outline
				)
				
				Spacer(modifier = Modifier.height(4.dp))
				
				if (chat.unreadCount > 0) {
					Badge {
						Text(chat.unreadCount.toString())
					}
				}
			}
		}
	}
}

@Composable
fun ChatRequestList(
	requests: List<ChatRequest>,
	onAccept: (String) -> Unit,
	onDecline: (String) -> Unit
) {
	LazyColumn {
		items(requests) { request ->
			ChatRequestItem(
				request = request,
				onAccept = { onAccept(request.id) },
				onDecline = { onDecline(request.id) }
			)
			Divider()
		}
	}
}

@Composable
fun ChatRequestItem(
	request: ChatRequest,
	onAccept: () -> Unit,
	onDecline: () -> Unit
) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = request.userName,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				
				Text(
					text = formatTime(request.timestamp),
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.outline
				)
			}
			
			Spacer(modifier = Modifier.height(4.dp))
			
			Text(
				text = "Subject: ${request.subject}",
				style = MaterialTheme.typography.bodyMedium,
				fontWeight = FontWeight.Medium
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = request.message,
				style = MaterialTheme.typography.bodyMedium
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.End
			) {
				OutlinedButton(
					onClick = onDecline,
					colors = ButtonDefaults.outlinedButtonColors(
						contentColor = MaterialTheme.colorScheme.error
					)
				) {
					Text("Decline")
				}
				
				Spacer(modifier = Modifier.width(8.dp))
				
				Button(onClick = onAccept) {
					Text("Accept")
				}
			}
		}
	}
}

// Helper function to format timestamps
fun formatTime(timestamp: Long): String {
	val now = System.currentTimeMillis()
	val diff = now - timestamp
	
	return when {
		diff < 60 * 1000 -> "Just now"
		diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
		diff < 24 * 60 * 60 * 1000 -> {
			SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
		}
		diff < 48 * 60 * 60 * 1000 -> "Yesterday"
		else -> {
			SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
		}
	}
}