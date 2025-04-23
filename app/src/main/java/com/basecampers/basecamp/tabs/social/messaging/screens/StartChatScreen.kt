package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.BasecampButton
import com.basecampers.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import com.basecampers.basecamp.ui.theme.*

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
				title = { 
					Text(
						"Start a New Chat",
						style = MaterialTheme.typography.titleLarge,
						color = TextPrimary,
						modifier = Modifier.fillMaxWidth(),
						textAlign = TextAlign.Center
					)
				},
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back",
							tint = TextPrimary
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = AppBackground
				)
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
				.background(AppBackground)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(24.dp)
		) {
			// Header Section
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = CardBackground
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Icon(
						imageVector = Icons.Default.Message,
						contentDescription = null,
						modifier = Modifier.size(48.dp),
						tint = SecondaryAqua
					)
					
					Spacer(modifier = Modifier.height(16.dp))
					
			Text(
				text = "Start a conversation with a BaseBuddy",
						style = MaterialTheme.typography.titleMedium,
						color = TextPrimary,
						fontWeight = FontWeight.Bold
					)
					
					Text(
						text = "Our support team is here to help you",
						style = MaterialTheme.typography.bodyMedium,
						color = TextSecondary
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
					BasecampButton(
						text = "Submit Chat Request",
				onClick = {
					try {
						onChatStarted("new-chat-1")
					} catch (e: Exception) {
					}
				},
				modifier = Modifier.fillMaxWidth()
					)
				}
			}
			
			// Topic Selection
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = CardBackground
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
			) {
					Text(
						text = "Select Topic",
						style = MaterialTheme.typography.titleSmall,
						color = TextPrimary,
						fontWeight = FontWeight.Bold
					)
					
					Spacer(modifier = Modifier.height(8.dp))
					
					OutlinedTextField(
						value = selectedTopic,
						onValueChange = { selectedTopic = it },
						modifier = Modifier.fillMaxWidth(),
						colors = TextFieldDefaults.outlinedTextFieldColors(
							focusedBorderColor = SecondaryAqua,
							unfocusedBorderColor = BorderColor
						)
					)
				}
			}
			
			// Message Input
			Card(
				modifier = Modifier.fillMaxWidth(),
				colors = CardDefaults.cardColors(
					containerColor = CardBackground
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Text(
						text = "Your Message",
						style = MaterialTheme.typography.titleSmall,
						color = TextPrimary,
						fontWeight = FontWeight.Bold
					)
					
					Spacer(modifier = Modifier.height(8.dp))
					
					OutlinedTextField(
						value = messageText,
						onValueChange = { messageText = it },
						modifier = Modifier.fillMaxWidth(),
						colors = TextFieldDefaults.outlinedTextFieldColors(
							focusedBorderColor = SecondaryAqua,
							unfocusedBorderColor = BorderColor
						),
						maxLines = 4
					)
				}
			}
			
			Spacer(modifier = Modifier.weight(1f))
		}
	}
}