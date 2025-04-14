package com.example.basecamp.tabs.social.messaging

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext

// StartChatScreen.kt
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartChatScreen(
	onChatStarted: (String) -> Unit,
	onNavigateBack: () -> Unit
) {
	val context = LocalContext.current
	val viewModel: UserMessagingViewModel = viewModel(
		factory = UserMessagingViewModel.Factory(context)
	)
	val scope = rememberCoroutineScope()
	
	var selectedTopic by remember { mutableStateOf("") }
	var messageText by remember { mutableStateOf("") }
	
	val topics = listOf("Booking Issue", "App Functionality", "Facility Maintenance", "Error Report")
	
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("New Conversation") },
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
				.padding(16.dp)
		) {
			Text(
				text = "Start a conversation with BaseBuddy",
				style = MaterialTheme.typography.titleLarge
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = "A BaseBuddy will respond to your message as soon as possible.",
				style = MaterialTheme.typography.bodyMedium
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Topic selection
			Text(
				text = "Select a topic:",
				style = MaterialTheme.typography.titleMedium
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			FlowRow(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				topics.forEach { topic ->
					FilterChip(
						selected = topic == selectedTopic,
						onClick = { selectedTopic = topic },
						label = { Text(topic) }
					)
				}
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Message input
			Text(
				text = "Your message:",
				style = MaterialTheme.typography.titleMedium
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			OutlinedTextField(
				value = messageText,
				onValueChange = { messageText = it },
				modifier = Modifier
					.fillMaxWidth()
					.height(150.dp),
				placeholder = { Text("Describe what you need help with...") },
				keyboardOptions = KeyboardOptions(
					capitalization = KeyboardCapitalization.Sentences
				)
			)
			
			Spacer(modifier = Modifier.weight(1f))
			
			// Submit button
			Button(
				onClick = {
					if (selectedTopic.isNotBlank() && messageText.isNotBlank()) {
						scope.launch {
							val chatId = viewModel.createChatRequest(selectedTopic, messageText)
							onChatStarted(chatId)
						}
					}
				},
				enabled = selectedTopic.isNotBlank() && messageText.isNotBlank(),
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Send Message")
			}
		}
	}
}