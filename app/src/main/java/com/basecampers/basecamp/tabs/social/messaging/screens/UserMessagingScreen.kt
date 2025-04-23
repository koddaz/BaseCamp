package com.basecampers.basecamp.tabs.social.messaging.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.HorizontalOptionCard
import com.basecampers.basecamp.tabs.social.messaging.viewModels.UserMessagingViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun UserMessagingScreen(
	onSelectActiveChat: (String) -> Unit,
	onSelectClosedChat: (String) -> Unit,
	onStartNewChat: () -> Unit,
	viewModel: UserMessagingViewModel = viewModel()
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(AppBackground)
	) {
		// Background Pattern
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(200.dp)
				.background(SecondaryAqua.copy(alpha = 0.1f))
		)

		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 24.dp)
		) {
			Spacer(modifier = Modifier.height(60.dp))

			// Header
			Text(
				text = "Messages",
				style = MaterialTheme.typography.headlineLarge.copy(
					fontWeight = FontWeight.Bold,
					fontSize = 32.sp
				),
				color = TextSecondary,
				modifier = Modifier.padding(bottom = 24.dp)
			)
			
			// Start New Chat Button
			HorizontalOptionCard(
				title = "Start a New Conversation",
				onClick = onStartNewChat,
				icon = Icons.Default.Message,
				iconBackground = SecondaryAqua,
				textColor = TextPrimary,
				textSize = 16f,
				textWeight = FontWeight.Bold
			)
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Active Chats Section
			Text(
				text = "Active Conversations",
				style = MaterialTheme.typography.titleMedium,
				color = TextPrimary,
				fontWeight = FontWeight.Bold
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			// Active Chat Card
			Card(
				onClick = { onSelectActiveChat("active-chat-1") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp),
				colors = CardDefaults.cardColors(
					containerColor = CardBackground
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						Column(modifier = Modifier.weight(1f)) {
							Text(
								text = "Support Chat",
								style = MaterialTheme.typography.titleSmall,
								color = TextPrimary,
								fontWeight = FontWeight.Bold
							)
							Spacer(modifier = Modifier.height(4.dp))
							Text(
								text = "Thanks for your help!",
								style = MaterialTheme.typography.bodyMedium,
								color = TextSecondary
							)
						}
						Text(
							text = "2m ago",
							style = MaterialTheme.typography.labelSmall,
							color = TextSecondary
						)
					}
				}
			}
			
			Spacer(modifier = Modifier.height(24.dp))
			
			// Chat History Section
			Text(
				text = "Chat History",
				style = MaterialTheme.typography.titleMedium,
				color = TextPrimary,
				fontWeight = FontWeight.Bold
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			// Closed Chat Card
			Card(
				onClick = { onSelectClosedChat("closed-chat-1") },
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp),
				colors = CardDefaults.cardColors(
					containerColor = CardBackground
				),
				elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						verticalAlignment = Alignment.CenterVertically
					) {
						Column(modifier = Modifier.weight(1f)) {
							Text(
								text = "Booking Issue",
								style = MaterialTheme.typography.titleSmall,
								color = TextPrimary,
								fontWeight = FontWeight.Bold
							)
							Spacer(modifier = Modifier.height(4.dp))
							Text(
								text = "Issue has been resolved. (Closed)",
								style = MaterialTheme.typography.bodyMedium,
								color = TextSecondary
							)
						}
						Text(
							text = "2d ago",
							style = MaterialTheme.typography.labelSmall,
							color = TextSecondary
						)
					}
				}
			}
		}
	}
}

@Composable
private fun EmptyStateMessage() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.padding(32.dp),
		contentAlignment = Alignment.Center
	) {
		Column(horizontalAlignment = Alignment.CenterHorizontally) {
			Icon(
				imageVector = Icons.Default.Message,
				contentDescription = null,
				modifier = Modifier.size(48.dp),
				tint = TextSecondary
			)
			
			Spacer(modifier = Modifier.height(16.dp))
			
			Text(
				text = "No conversations yet",
				style = MaterialTheme.typography.bodyLarge,
				color = TextSecondary
			)
		}
	}
}