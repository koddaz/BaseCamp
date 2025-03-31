package com.example.basecamp.tabs.social.qna

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAScreen(
	onNavigateBack: () -> Unit
) {
	val questions = remember {
		listOf(
			QnA(
				"How do I book a training session?",
				"You can book training sessions through the Booking tab or from the facility page. Select your preferred time slot and confirm your booking. You'll receive a confirmation notification once your booking is successful."
			),
			QnA(
				"What is the cancellation policy?",
				"You can cancel bookings up to 24 hours before the scheduled time without penalty. Cancellations made within 24 hours may incur a fee depending on the facility policy."
			),
			QnA(
				"How do I reset my password?",
				"To reset your password, go to the Profile tab and select 'Account Settings'. From there, you can select 'Change Password' and follow the instructions to set a new password."
			),
			QnA(
				"What are BaseBuddies?",
				"BaseBuddies are specially trained staff members who can provide assistance with app functionality, booking issues, and general facility information. They're here to help improve your experience with the app and facilities."
			),
			QnA(
				"How do I contact support?",
				"You can contact support through the Messages section in the Social tab. Select 'New Chat Request' to connect with a BaseBuddy who can assist you with your questions or concerns."
			)
		)
	}
	
	var searchQuery by remember { mutableStateOf("") }
	var filteredQuestions by remember { mutableStateOf(questions) }
	
	LaunchedEffect(searchQuery) {
		filteredQuestions = if (searchQuery.isBlank()) {
			questions
		} else {
			questions.filter {
				it.question.contains(searchQuery, ignoreCase = true) ||
						it.answer.contains(searchQuery, ignoreCase = true)
			}
		}
	}
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Q&A") },
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
			// Search bar
			OutlinedTextField(
				value = searchQuery,
				onValueChange = { searchQuery = it },
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp),
				placeholder = { Text("Search questions...") },
				leadingIcon = {
					Icon(
						imageVector = Icons.Default.Search,
						contentDescription = "Search"
					)
				},
				singleLine = true
			)
			
			LazyColumn {
				items(filteredQuestions) { qna ->
					QnAExpandableItem(qna = qna)
					Divider()
				}
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAExpandableItem(qna: QnA) {
	var expanded by remember { mutableStateOf(false) }
	
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		onClick = { expanded = !expanded },
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				text = qna.question,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			
			if (expanded) {
				Spacer(modifier = Modifier.height(8.dp))
				Divider()
				Spacer(modifier = Modifier.height(8.dp))
				
				Text(
					text = qna.answer,
					style = MaterialTheme.typography.bodyMedium
				)
			}
		}
	}
}

data class QnA(
	val question: String,
	val answer: String
)