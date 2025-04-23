package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.BasecampButton
import com.basecampers.basecamp.tabs.social.messaging.MessageItem
import com.basecampers.basecamp.tabs.social.messaging.models.Message
import com.basecampers.basecamp.tabs.social.messaging.viewModels.ChatViewModel
import com.basecampers.basecamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
	chatId: String,
	isReadOnly: Boolean,
	onNavigateBack: () -> Unit,
	viewModel: ChatViewModel = viewModel()
) {
	var messageText by remember { mutableStateOf("") }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { 
					Column {
						Text(
							"Support Team",
							style = MaterialTheme.typography.titleMedium,
							color = AppBackground
						)
						Text(
							"Online",
							style = MaterialTheme.typography.bodySmall,
							color = AppBackground.copy(alpha = 0.7f)
						)
					}
				},
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back",
							tint = AppBackground
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = SecondaryAqua,
					titleContentColor = AppBackground,
					navigationIconContentColor = AppBackground
				)
			)
		},
		floatingActionButton = {} // Hide FAB
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
				.background(AppBackground)
		) {
			// Messages area
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.padding(horizontal = 16.dp, vertical = 8.dp)
			) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					// Date separator
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(vertical = 8.dp),
						contentAlignment = Alignment.Center
					) {
						Text(
							"Today",
							style = MaterialTheme.typography.bodySmall,
							color = TextSecondary,
							modifier = Modifier
								.background(
									color = CardBackground,
									shape = RoundedCornerShape(12.dp)
								)
								.padding(horizontal = 12.dp, vertical = 4.dp)
						)
					}

					// System message
					MessageItem(
						message = Message(
							content = "Welcome to the chat! This is a placeholder message.",
							senderName = "System",
							timestamp = System.currentTimeMillis()
						),
						isFromCurrentUser = false
					)

					// User message
					MessageItem(
						message = Message(
							content = "Hello, I need help with my booking.",
							senderName = "You",
							timestamp = System.currentTimeMillis()
						),
						isFromCurrentUser = true
					)

					// Support message
					MessageItem(
						message = Message(
							content = "Hi! I'm here to help. Could you please tell me more about your booking issue?",
							senderName = "Support Team",
							timestamp = System.currentTimeMillis()
						),
						isFromCurrentUser = false
					)
				}
			}
			
			// Input area
			if (!isReadOnly) {
				Card(
					modifier = Modifier.fillMaxWidth(),
					colors = CardDefaults.cardColors(
						containerColor = CardBackground
					),
					elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
				) {
					Column {
						// Typing indicator
						if (true) { // Replace with actual typing state
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.padding(horizontal = 16.dp, vertical = 4.dp),
								verticalAlignment = Alignment.CenterVertically
							) {
								Text(
									"Support Team is typing...",
									style = MaterialTheme.typography.bodySmall,
									color = TextSecondary
								)
							}
						}

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
								colors = TextFieldDefaults.outlinedTextFieldColors(
									focusedBorderColor = SecondaryAqua,
									unfocusedBorderColor = BorderColor,
									focusedLabelColor = SecondaryAqua,
									unfocusedLabelColor = TextSecondary
								),
								shape = RoundedCornerShape(24.dp)
							)
							
							Spacer(modifier = Modifier.width(8.dp))
							
							IconButton(
								onClick = {
									if (messageText.isNotBlank()) {
										messageText = ""
									}
								},
								colors = IconButtonDefaults.iconButtonColors(
									containerColor = SecondaryAqua,
									contentColor = AppBackground
								),
								modifier = Modifier
									.size(48.dp)
									.clip(CircleShape)
							) {
								Icon(
									imageVector = Icons.Default.Send,
									contentDescription = "Send"
								)
							}
						}
					}
				}
			} else {
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(16.dp),
					contentAlignment = Alignment.Center
				) {
					Text(
						"This chat is read-only",
						style = MaterialTheme.typography.bodyLarge,
						color = TextSecondary
					)
				}
			}
		}
	}
}