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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
	onNavigateBack: () -> Unit
) {
	var showClosedChats by remember { mutableStateOf(false) }
	var selectedTab by remember { mutableStateOf(0) }
	
	val tabs = listOf("Active Chats", "Chat Requests")
	
	val activeChats = remember {
		listOf(
			Chat(
				id = "1",
				name = "John (BaseBuddy)",
				lastMessage = "I've checked the booking system and your session is confirmed",
				time = "12:30 PM",
				unreadCount = 2,
				isActive = true,
				isBaseBuddy = true
			),
			Chat(
				id = "2",
				name = "Emma (BaseBuddy)",
				lastMessage = "Let me check that for you and get back to you shortly",
				time = "Yesterday",
				unreadCount = 0,
				isActive = true,
				isBaseBuddy = true
			),
			Chat(
				id = "3",
				name = "Michael (BaseBuddy)",
				lastMessage = "Your membership has been updated. You can now access premium facilities",
				time = "3 days ago",
				unreadCount = 0,
				isActive = false,
				isBaseBuddy = true
			)
		)
	}
	
	val chatRequests = remember {
		listOf(
			ChatRequest(
				id = "1",
				userName = "Alex",
				subject = "Booking Issue",
				message = "Hello, I need help with a booking that was canceled automatically",
				time = "5 min ago"
			),
			ChatRequest(
				id = "2",
				userName = "Taylor",
				subject = "App Functionality",
				message = "I'm having trouble with the payment system in the app",
				time = "20 min ago"
			)
		)
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
					if (true) { // Replace with actual role check
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
			if (selectedTab == 0) { // Only show in active chats tab
				FloatingActionButton(
					onClick = { /* Handle new chat request */ }
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
			// Tabs
			TabRow(selectedTabIndex = selectedTab) {
				tabs.forEachIndexed { index, title ->
					Tab(
						selected = selectedTab == index,
						onClick = { selectedTab = index },
						text = { Text(title) }
					)
				}
			}
			
			when (selectedTab) {
				0 -> {
					// Active Chats Tab
					val filteredChats = if (showClosedChats) {
						activeChats
					} else {
						activeChats.filter { it.isActive }
					}
					
					if (filteredChats.isEmpty()) {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = "No active chats",
								style = MaterialTheme.typography.bodyLarge
							)
						}
					} else {
						LazyColumn {
							items(filteredChats) { chat ->
								ChatItem(chat = chat)
								Divider()
							}
						}
					}
				}
				1 -> {
					// Chat Requests Tab (BaseBuddy/Admin View)
					if (chatRequests.isEmpty()) {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = "No pending chat requests",
								style = MaterialTheme.typography.bodyLarge
							)
						}
					} else {
						LazyColumn {
							items(chatRequests) { request ->
								ChatRequestItem(
									request = request,
									onAccept = { /* Handle accept */ },
									onDecline = { /* Handle decline */ }
								)
								Divider()
							}
						}
					}
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatItem(chat: Chat) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		onClick = { /* Navigate to chat detail */ },
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
				if (chat.isBaseBuddy) {
					Icon(
						imageVector = Icons.Default.Support,
						contentDescription = "BaseBuddy",
						tint = MaterialTheme.colorScheme.onPrimaryContainer
					)
				} else {
					Text(
						text = chat.name.first().toString(),
						style = MaterialTheme.typography.titleMedium,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
			
			Spacer(modifier = Modifier.width(16.dp))
			
			Column(modifier = Modifier.weight(1f)) {
				Row(
					verticalAlignment = Alignment.CenterVertically
				) {
					Text(
						text = chat.name,
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
					text = chat.lastMessage,
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
					text = chat.time,
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
					text = request.time,
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

data class Chat(
	val id: String,
	val name: String,
	val lastMessage: String,
	val time: String,
	val unreadCount: Int,
	val isActive: Boolean,
	val isBaseBuddy: Boolean
)

data class ChatRequest(
	val id: String,
	val userName: String,
	val subject: String,
	val message: String,
	val time: String
)