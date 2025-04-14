package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.components.MessageItem
import com.example.basecamp.tabs.social.messaging.viewModels.ChatViewModel
import kotlinx.coroutines.launch

// ChatScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
	chatId: String,
	isReadOnly: Boolean,
	onNavigateBack: () -> Unit,
	viewModel: ChatViewModel = viewModel()
) {
	// Get current context
	val context = LocalContext.current
	
	// Create the ViewModel with the factory
	val viewModel: ChatViewModel = viewModel(
		factory = ChatViewModel.Factory(context)
	)
	
	val scope = rememberCoroutineScope()
	
	val chatInfo by viewModel.getChatInfo(chatId).collectAsState(initial = null)
	val messages by viewModel.getMessages(chatId).collectAsState(initial = emptyList())
	val isPending by viewModel.isPendingChat(chatId).collectAsState(initial = false)
	
	var messageText by remember { mutableStateOf("") }
	var showCloseDialog by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }
	
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Column {
						Text(chatInfo?.title ?: "Loading...")
						if (isPending) {
							Text(
								text = "Pending Response",
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.primary
							)
						}
					}
				},
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				actions = {
					if (chatInfo != null) {  // Only show actions when chat is loaded
						if (!isReadOnly && !isPending) {
							IconButton(onClick = { showCloseDialog = true }) {
								Icon(
									imageVector = Icons.Default.Close,
									contentDescription = "Close Chat"
								)
							}
						} else if (isReadOnly) {
							IconButton(onClick = { showDeleteDialog = true }) {
								Icon(
									imageVector = Icons.Default.Delete,
									contentDescription = "Delete Chat"
								)
							}
						}
					}
				}
			)
		}
	) { paddingValues ->
		// Show loading indicator if chatInfo is null
		if (chatInfo == null) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(paddingValues),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		} else {
			Column(
				modifier = Modifier
					.padding(paddingValues)
					.fillMaxSize()
			) {
				// Messages list
				LazyColumn(
					modifier = Modifier
						.weight(1f)
						.fillMaxWidth()
						.padding(horizontal = 16.dp),
					reverseLayout = true
				) {
					items(messages.reversed()) { message ->
						MessageItem(
							message = message,
							isFromCurrentUser = message.senderId == viewModel.getCurrentUserId()
						)
						
						Spacer(modifier = Modifier.height(8.dp))
					}
					
					// Empty state for new chat or no messages
					if (messages.isEmpty()) {
						item {
							Box(
								modifier = Modifier
									.fillParentMaxSize()
									.padding(32.dp),
								contentAlignment = Alignment.Center
							) {
								Text(
									text = if (isPending)
										"Send a message to start the conversation"
									else
										"No messages yet",
									style = MaterialTheme.typography.bodyLarge,
									color = MaterialTheme.colorScheme.outline,
									textAlign = TextAlign.Center
								)
							}
						}
					}
				}
				
				// Input field (hidden if read-only)
				if (!isReadOnly) {
					Card(
						modifier = Modifier.fillMaxWidth(),
						elevation = CardDefaults.cardElevation(
							defaultElevation = 4.dp
						)
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(8.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							OutlinedTextField(
								value = messageText,
								onValueChange = { messageText = it },
								modifier = Modifier.weight(1f),
								placeholder = { Text("Type a message...") },
								maxLines = 3,
								keyboardOptions = KeyboardOptions(
									capitalization = KeyboardCapitalization.Sentences
								)
							)
							
							Spacer(modifier = Modifier.width(8.dp))
							
							IconButton(
								onClick = {
									if (messageText.isNotBlank()) {
										viewModel.sendMessage(chatId, messageText)
										messageText = ""
									}
								}
							) {
								Icon(
									imageVector = Icons.Default.Send,
									contentDescription = "Send"
								)
							}
						}
					}
				} else {
					// Read-only info bar for closed chats
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp),
						colors = CardDefaults.cardColors(
							containerColor = MaterialTheme.colorScheme.surfaceVariant
						)
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(16.dp),
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = "This chat has been closed",
								style = MaterialTheme.typography.bodyMedium,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				}
			}
			
			// For the close chat dialog
			if (showCloseDialog) {
				AlertDialog(
					onDismissRequest = { showCloseDialog = false },
					title = { Text("Close Chat") },
					text = { Text("Are you sure you want to close this chat? You won't be able to send new messages.") },
					confirmButton = {
						TextButton(
							onClick = {
								// Launch a coroutine to call the suspend function
								scope.launch {
									viewModel.closeChat(chatId)
								}
								showCloseDialog = false
								onNavigateBack()
							}
						) {
							Text("Close Chat")
						}
					},
					dismissButton = {
						TextButton(onClick = { showCloseDialog = false }) {
							Text("Cancel")
						}
					}
				)
			}

// For the delete chat dialog
			if (showDeleteDialog) {
				AlertDialog(
					onDismissRequest = { showDeleteDialog = false },
					title = { Text("Delete Chat") },
					text = { Text("Are you sure you want to delete this chat? This action cannot be undone.") },
					confirmButton = {
						TextButton(
							onClick = {
								// Launch a coroutine to call the suspend function
								scope.launch {
									viewModel.deleteChat(chatId)
								}
								showDeleteDialog = false
								onNavigateBack()
							}
						) {
							Text("Delete")
						}
					},
					dismissButton = {
						TextButton(onClick = { showDeleteDialog = false }) {
							Text("Cancel")
						}
					}
				)
			}
		}
	}
}