package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
				title = { Text("Chat") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = PrimaryRed,
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
					.padding(16.dp),
				contentAlignment = Alignment.Center
			) {
				Column(
					modifier = Modifier.fillMaxWidth(),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					MessageItem(
						message = Message(
							content = "Welcome to the chat! This is a placeholder message.",
							senderName = "System",
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
								unfocusedBorderColor = BorderColor
							)
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
							)
						) {
							Icon(
								imageVector = Icons.Default.Send,
								contentDescription = "Send"
							)
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