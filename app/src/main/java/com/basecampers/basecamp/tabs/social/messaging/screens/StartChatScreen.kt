package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.BasecampButton
import com.basecampers.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
	onChatStarted: (String) -> Unit,
	onNavigateBack: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	var selectedTopic by remember { mutableStateOf("Booking Issue") }
	var messageText by remember { mutableStateOf("Hello, I need help with...") }
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Start a New Chat") },
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
				text = "Start a conversation with a BaseBuddy",
				style = MaterialTheme.typography.titleMedium
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
			BasecampButton(
				text = "Submit Chat Request",
				onClick = {
					try {
						onChatStarted("new-chat-1")
					} catch (e: Exception) {
					}
				}
			)
		}
	}
}