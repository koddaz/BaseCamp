package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRequestScreen(
	chatId: String,
	onAccept: () -> Unit,
	onDecline: () -> Unit,
	viewModel: SuperUserMessagingViewModel = viewModel()
) {
	val pendingChat = viewModel.getPendingChatById(chatId)
	val chatRequest by viewModel.getChatRequestDetails(chatId).collectAsState(initial = null)
	
	viewModel.markChatAsRead(chatId)
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Chat Request") },
				navigationIcon = {
					IconButton(onClick = onDecline) {
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
				.padding(16.dp)
		) {
			if (pendingChat != null) {
				Card(
					modifier = Modifier.fillMaxWidth()
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Text(
							text = "Request from ${pendingChat.userName}",
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Bold
						)
						
						Spacer(modifier = Modifier.height(8.dp))
						
						Text(
							text = "Subject: ${pendingChat.subject}",
							style = MaterialTheme.typography.bodyLarge,
							fontWeight = FontWeight.Medium
						)
						
						Spacer(modifier = Modifier.height(16.dp))
						
						Text(
							text = chatRequest?.message ?: "I need help with something.",
							style = MaterialTheme.typography.bodyMedium
						)
						
						Spacer(modifier = Modifier.height(16.dp))
						
						Text(
							text = "Received ${pendingChat.timeReceived}",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.outline
						)
					}
				}
				
				Spacer(modifier = Modifier.weight(1f))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					OutlinedButton(
						onClick = onDecline,
						modifier = Modifier.weight(1f),
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = MaterialTheme.colorScheme.error
						)
					) {
						Text("Decline Chat")
					}
					
					Button(
						onClick = {
							viewModel.acceptChatRequest(chatId)
							onAccept()
						},
						modifier = Modifier.weight(1f)
					) {
						Text("Accept Chat")
					}
				}
			} else {
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					Text("Chat request not found")
				}
			}
		}
	}
}