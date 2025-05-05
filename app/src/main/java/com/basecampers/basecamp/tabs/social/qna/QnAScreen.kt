package com.basecampers.basecamp.tabs.social.qna

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.social.models.QnAItem
import com.basecampers.basecamp.tabs.social.viewModel.QnAViewModel
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAScreen(
	isPrivilegedUser: Boolean,
	qnaViewModel: QnAViewModel = viewModel()
) {
	// State for dialogs
	var showAddEditDialog by remember { mutableStateOf(false) }
	var showDeleteDialog by remember { mutableStateOf(false) }
	var currentQnAItem by remember { mutableStateOf<QnAItem?>(null) }
	
	// Collect state from ViewModel
	val qnaItems by qnaViewModel.qnaItems.collectAsState()
	val isLoading by qnaViewModel.isLoading.collectAsState()
	
	// Load Q&A items when screen appears
	LaunchedEffect(Unit) {
		qnaViewModel.fetchQnAItems()
	}
	
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
				text = "Frequently Asked Questions",
				style = MaterialTheme.typography.headlineLarge.copy(
					fontWeight = FontWeight.Bold,
					fontSize = 32.sp
				),
				color = TextSecondary,
				modifier = Modifier.padding(bottom = 24.dp)
			)
			
			if (isLoading) {
				// Loading indicator
				Box(
					modifier = Modifier.fillMaxSize(),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator(
						color = SecondaryAqua
					)
				}
			} else {
				// List of Q&A items
				LazyColumn(
					modifier = Modifier.fillMaxSize(),
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					// Separate published and draft items
					val publishedItems = qnaItems.filter { it.isPublished }
					val draftItems = qnaItems.filter { !it.isPublished }
					
					// Show message if no items at all
					if (qnaItems.isEmpty()) {
						item {
							Box(
								modifier = Modifier
									.fillMaxWidth()
									.height(200.dp),
								contentAlignment = Alignment.Center
							) {
								Text(
									text = "No questions found.",
									style = MaterialTheme.typography.bodyLarge,
									color = TextSecondary
								)
							}
						}
					} else {
						// Published items section
						if (publishedItems.isEmpty()) {
							item {
								Box(
									modifier = Modifier
										.fillMaxWidth()
										.height(100.dp),
									contentAlignment = Alignment.Center
								) {
									Text(
										text = "No published questions available.",
										style = MaterialTheme.typography.bodyLarge,
										color = TextSecondary
									)
								}
							}
						} else {
							items(publishedItems) { item ->
								QnAItemCard(
									item = item,
									isPrivilegedUser = isPrivilegedUser,
									onEdit = {
										currentQnAItem = item
										showAddEditDialog = true
									},
									onDelete = {
										currentQnAItem = item
										showDeleteDialog = true
									}
								)
							}
						}
						
						// Draft items section - only visible to privileged users
						if (isPrivilegedUser && draftItems.isNotEmpty()) {
							item {
								Spacer(modifier = Modifier.height(16.dp))
								Text(
									text = "Drafts",
									style = MaterialTheme.typography.titleLarge.copy(
										fontWeight = FontWeight.Bold
									),
									color = TextSecondary,
									modifier = Modifier.padding(vertical = 16.dp)
								)
							}
							
							items(draftItems) { item ->
								QnAItemCard(
									item = item,
									isPrivilegedUser = isPrivilegedUser,
									onEdit = {
										currentQnAItem = item
										showAddEditDialog = true
									},
									onDelete = {
										currentQnAItem = item
										showDeleteDialog = true
									}
								)
							}
						}
						
						// Add extra space at the bottom for scrolling past floating buttons
						item {
							Spacer(modifier = Modifier.height(100.dp))
						}
					}
				}
			}
		}
		
		// FAB for adding new QnA item
		if (isPrivilegedUser) {
			FloatingActionButton(
				onClick = {
					currentQnAItem = null
					showAddEditDialog = true
				},
				modifier = Modifier
					.align(Alignment.BottomEnd)
					.padding(16.dp),
				containerColor = SecondaryAqua
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Add Question",
					tint = Color.White
				)
			}
		}
	}
	
	// Add/Edit Dialog
	if (showAddEditDialog) {
		QnAEditDialog(
			qnaItem = currentQnAItem,
			onDismiss = { showAddEditDialog = false },
			onSave = { question, answer, isPublished ->
				if (currentQnAItem == null) {
					// Add new item
					qnaViewModel.addQnAItem(
						question = question,
						answer = answer,
						isPublished = isPublished,
						onSuccess = { showAddEditDialog = false },
						onError = {}
					)
				} else {
					// Update existing item
					qnaViewModel.updateQnAItem(
						id = currentQnAItem!!.id,
						question = question,
						answer = answer,
						isPublished = isPublished,
						onSuccess = { showAddEditDialog = false },
						onError = {}
					)
				}
			}
		)
	}
	
	// Delete Confirmation Dialog
	if (showDeleteDialog && currentQnAItem != null) {
		DeleteConfirmationDialog(
			onDismiss = { showDeleteDialog = false },
			onConfirm = {
				qnaViewModel.deleteQnAItem(
					id = currentQnAItem!!.id,
					onSuccess = {
						showDeleteDialog = false
					},
					onError = {}
				)
			}
		)
	}
}

@Composable
fun QnAItemCard(
	item: QnAItem,
	isPrivilegedUser: Boolean,
	onEdit: () -> Unit,
	onDelete: () -> Unit
) {
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
				text = item.question,
				style = MaterialTheme.typography.titleMedium,
				color = TextPrimary,
				fontWeight = FontWeight.Bold
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				text = item.answer,
				style = MaterialTheme.typography.bodyMedium,
				color = TextSecondary
			)
			if (isPrivilegedUser) {
				Spacer(modifier = Modifier.height(16.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					if (!item.isPublished) {
						Text(
							text = "Draft",
							style = MaterialTheme.typography.bodySmall,
							color = SecondaryAqua,
							modifier = Modifier
								.padding(end = 8.dp)
								.align(Alignment.CenterVertically)
						)
					}
					TextButton(onClick = onEdit) {
						Text("Edit")
					}
					TextButton(onClick = onDelete) {
						Text("Delete")
					}
				}
			}
		}
	}
}