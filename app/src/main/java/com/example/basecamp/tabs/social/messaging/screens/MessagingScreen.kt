package com.example.basecamp.tabs.social.messaging.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.messaging.components.formatTime
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
	isSuper: Boolean,
	onNavigateBack: () -> Unit,
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onSelectChatRequest: (String) -> Unit,
	onStartNewChat: () -> Unit
) {
	val context = LocalContext.current
	val scope = rememberCoroutineScope()
	
	// Initialize ViewModels with factories
	val userViewModel: UserMessagingViewModel = viewModel(
		factory = UserMessagingViewModel.Factory(context)
	)
	val superViewModel: SuperUserMessagingViewModel = viewModel(
		factory = SuperUserMessagingViewModel.Factory(context)
	)
	
	var showClosedChats by remember { mutableStateOf(false) }
	var selectedTab by remember { mutableStateOf(0) }
	
	val tabs = if (isSuper) {
		listOf("Active Chats", "Chat Requests")
	} else {
		listOf("My Chats")
	}
	
	// Get data from appropriate ViewModel based on user role
	val chats = if (isSuper) {
		if (showClosedChats) {
			// Show both active and closed chats
			val activeChats by superViewModel.getAssignedChats().collectAsState(initial = emptyList())
			val closedChats by superViewModel.getClosedChats().collectAsState(initial = emptyList())
			remember(activeChats, closedChats) { activeChats + closedChats }
		} else {
			// Show only active chats
			val activeChats by superViewModel.getAssignedChats().collectAsState(initial = emptyList())
			activeChats
		}
	} else {
		val activeChats by userViewModel.getActiveChats().collectAsState(initial = emptyList())
		val closedChats by userViewModel.getClosedChats().collectAsState(initial = emptyList())
		remember(activeChats, closedChats) { activeChats + closedChats }
	}
	
	val pendingChats by superViewModel.getPendingChats().collectAsState(initial = emptyList())
	
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
					if (chats.isEmpty()) {
						EmptyState(message = "No active chats")
					} else {
						ChatInfoList(
							chats = chats,
							onChatSelected = { chat ->
								if (chat.status == com.example.basecamp.tabs.social.messaging.models.ChatStatus.ACTIVE) {
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
					if (pendingChats.isEmpty()) {
						EmptyState(message = "No pending chat requests")
					} else {
						ChatInfoList(
							chats = pendingChats,
							onChatSelected = { chatInfo ->
								scope.launch {
									// Use the acceptChatRequestWithResponse method
									superViewModel.acceptChatRequestWithResponse(
										chatInfo.id,
										"I'm here to help. What can I do for you?"
									)
									onSelectChatRequest(chatInfo.id)
								}
							}
						)
					}
				}
				
				// Regular User - My Chats
				else -> {
					val activeChats = chats.filter { it.status == com.example.basecamp.tabs.social.messaging.models.ChatStatus.ACTIVE }
					val closedChats = chats.filter { it.status == com.example.basecamp.tabs.social.messaging.models.ChatStatus.CLOSED }
					
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
								
								ChatInfoList(
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
								
								ChatInfoList(
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
fun ChatInfoList(
	chats: List<ChatInfo>,
	onChatSelected: (ChatInfo) -> Unit
) {
	LazyColumn {
		items(chats) { chat ->
			ChatInfoItem(
				chat = chat,
				onClick = { onChatSelected(chat) }
			)
			Divider()
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoItem(
	chat: ChatInfo,
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
					
					if (chat.status == com.example.basecamp.tabs.social.messaging.models.ChatStatus.CLOSED) {
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