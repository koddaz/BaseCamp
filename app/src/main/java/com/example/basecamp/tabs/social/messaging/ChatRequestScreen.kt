package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.components.formatTime
import com.example.basecamp.tabs.social.messaging.viewModels.SuperUserMessagingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRequestScreen(
	chatId: String,
	onAccept: () -> Unit,
	onDecline: () -> Unit
) {
	val context = LocalContext.current
	val viewModel: SuperUserMessagingViewModel = viewModel(
		factory = SuperUserMessagingViewModel.Factory(context)
	)
	val scope = rememberCoroutineScope()
	
	val chatInfo by viewModel.getChatRequestDetails(chatId).collectAsState(initial = null)
	var responseText by remember { mutableStateOf("") }
	var showDeclineDialog by remember { mutableStateOf(false) }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Chat Request") },
				navigationIcon = {
					IconButton(onClick = { showDeclineDialog = true }) {
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
			chatInfo?.let { info ->
				// Request details
				Card(
					modifier = Modifier.fillMaxWidth()
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							Text(
								text = info.title,
								style = MaterialTheme.typography.titleMedium,
								fontWeight = FontWeight.SemiBold
							)
							
							Text(
								text = formatTime(info.lastMessageTime),
								style = MaterialTheme.typography.bodySmall,
								color = MaterialTheme.colorScheme.outline
							)
						}
						
						Spacer(modifier = Modifier.height(8.dp))
						
						Text(
							text = "Subject: ${info.subject}",
							style = MaterialTheme.typography.titleSmall,
							fontWeight = FontWeight.Medium
						)
						
						Spacer(modifier = Modifier.height(16.dp))
						
						Card(
							modifier = Modifier.fillMaxWidth(),
							colors = CardDefaults.cardColors(
								containerColor = MaterialTheme.colorScheme.surfaceVariant
							)
						) {
							Text(
								text = info.lastMessageText,
								style = MaterialTheme.typography.bodyMedium,
								modifier = Modifier.padding(16.dp)
							)
						}
					}
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				// Response section
				Text(
					text = "Your Response",
					style = MaterialTheme.typography.titleMedium
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				OutlinedTextField(
					value = responseText,
					onValueChange = { responseText = it },
					modifier = Modifier
						.fillMaxWidth()
						.height(150.dp),
					placeholder = { Text("Type your response here") },
					keyboardOptions = KeyboardOptions(
						capitalization = KeyboardCapitalization.Sentences
					)
				)
				
				Spacer(modifier = Modifier.weight(1f))
				
				// Action buttons
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					OutlinedButton(
						onClick = { showDeclineDialog = true },
						modifier = Modifier.weight(1f),
						colors = ButtonDefaults.outlinedButtonColors(
							contentColor = MaterialTheme.colorScheme.error
						)
					) {
						Text("Decline")
					}
					
					Button(
						onClick = {
							if (responseText.isNotBlank()) {
								scope.launch {
									viewModel.acceptChatRequestWithResponse(chatId, responseText)
									onAccept()
								}
							}
						},
						enabled = responseText.isNotBlank(),
						modifier = Modifier.weight(1f)
					) {
						Text("Accept & Respond")
					}
				}
			} ?: run {
				// Loading or error state
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}
		}
	}
	
	// Decline confirmation dialog
	if (showDeclineDialog) {
		AlertDialog(
			onDismissRequest = { showDeclineDialog = false },
			title = { Text("Decline Request") },
			text = { Text("Are you sure you want to decline this chat request? The user will need to create a new request.") },
			confirmButton = {
				TextButton(
					onClick = {
						scope.launch {
							viewModel.declineChat(chatId)
							onDecline()
						}
					}
				) {
					Text("Decline")
				}
			},
			dismissButton = {
				TextButton(onClick = { showDeclineDialog = false }) {
					Text("Cancel")
				}
			}
		)
	}
}