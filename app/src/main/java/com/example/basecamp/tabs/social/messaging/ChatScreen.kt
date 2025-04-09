package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.components.MessageItem
import com.example.basecamp.tabs.social.messaging.viewModels.ChatViewModel
import com.example.basecamp.tabs.social.messaging.models.Message
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
	chatId: String,
	isReadOnly: Boolean,
	onNavigateBack: () -> Unit,
	viewModel: ChatViewModel = viewModel()
) {
	val chatInfo by viewModel.getChatInfo(chatId).collectAsState(initial = null)
	val messages by viewModel.getMessages(chatId).collectAsState(initial = emptyList())
	
	var messageText by remember { mutableStateOf("") }
	var showCloseDialog by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Text(chatInfo?.title ?: "Chat")
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
					if (!isReadOnly) {
						// Close chat action for active chats
						IconButton(onClick = { showCloseDialog = true }) {
							Icon(
								imageVector = Icons.Default.Close,
								contentDescription = "Close Chat"
							)
						}
					} else {
						// Delete chat action for closed chats
						IconButton(onClick = { showDeleteDialog = true }) {
							Icon(
								imageVector = Icons.Default.Delete,
								contentDescription = "Delete Chat"
							)
						}
					}
				}
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			// Messages
			LazyColumn(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.padding(16.dp),
				reverseLayout = true
			) {
				items(messages.reversed()) { message ->
					MessageItem(
						message = message,
						isFromCurrentUser = message.senderId == viewModel.getCurrentUserId()
					)
					
					Spacer(modifier = Modifier.height(8.dp))
				}
			}
			
			// Input field (if not read-only)
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
							maxLines = 3
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
		
		// Close Chat Dialog
		if (showCloseDialog) {
			AlertDialog(
				onDismissRequest = { showCloseDialog = false },
				title = { Text("Close Chat") },
				text = { Text("Are you sure you want to close this chat? You won't be able to send new messages.") },
				confirmButton = {
					TextButton(
						onClick = {
							viewModel.closeChat(chatId)
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
		
		// Delete Chat Dialog
		if (showDeleteDialog) {
			AlertDialog(
				onDismissRequest = { showDeleteDialog = false },
				title = { Text("Delete Chat") },
				text = { Text("Are you sure you want to delete this chat? This action cannot be undone.") },
				confirmButton = {
					TextButton(
						onClick = {
							viewModel.deleteChat(chatId)
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