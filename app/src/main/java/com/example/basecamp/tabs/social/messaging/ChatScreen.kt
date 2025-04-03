package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
	}
}

@Composable
fun MessageItem(
	message: Message,
	isFromCurrentUser: Boolean
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
	) {
		Column(
			modifier = Modifier
				.widthIn(max = 300.dp)
				.clip(
					RoundedCornerShape(
						topStart = 16.dp,
						topEnd = 16.dp,
						bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
						bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
					)
				)
				.background(
					if (isFromCurrentUser)
						MaterialTheme.colorScheme.primary
					else
						MaterialTheme.colorScheme.surfaceVariant
				)
				.padding(12.dp)
		) {
			Text(
				text = message.content,
				color = if (isFromCurrentUser)
					MaterialTheme.colorScheme.onPrimary
				else
					MaterialTheme.colorScheme.onSurfaceVariant
			)
			
			Spacer(modifier = Modifier.height(4.dp))
			
			Text(
				text = message.senderName,
				style = MaterialTheme.typography.labelSmall,
				fontWeight = FontWeight.Bold,
				color = if (isFromCurrentUser)
					MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
				else
					MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
			)
		}
		
		Spacer(modifier = Modifier.height(2.dp))
		
		Text(
			text = timeString(message.timestamp),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.outline
		)
	}
}

private fun timeString(timestamp: Long): String {
	val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
	return dateFormat.format(Date(timestamp))
}