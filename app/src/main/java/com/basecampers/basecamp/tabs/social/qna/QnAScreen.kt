package com.basecampers.basecamp.tabs.social.qna

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.social.models.QnAItem
import com.basecampers.basecamp.tabs.social.viewModel.QnAViewModel

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
	
	Box(modifier = Modifier.fillMaxSize()) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp)
		) {
			// Q&A content
			if (isLoading) {
				// Loading indicator
				Box(
					modifier = Modifier
						.fillMaxSize()
						.weight(1f),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			} else {
				// List of Q&A items
				LazyColumn(
					modifier = Modifier
						.fillMaxSize()
						.weight(1f)
				) {
					// Show published items for everyone, drafts only for privileged users
					val filteredItems = if (isPrivilegedUser) {
						qnaItems
					} else {
						qnaItems.filter { it.isPublished }
					}
					
					if (filteredItems.isEmpty()) {
						item {
							Box(
								modifier = Modifier
									.fillMaxWidth()
									.height(200.dp),
								contentAlignment = Alignment.Center
							) {
								Text("No Q&A items found.")
							}
						}
					} else {
						items(filteredItems) { item ->
							QnAItemView(
								item = item,
								isPrivilegedUser = isPrivilegedUser,
								onEditClick = {
									currentQnAItem = item
									showAddEditDialog = true
								},
								onDeleteClick = {
									currentQnAItem = item
									showDeleteDialog = true
								},
								onPublishToggle = { isPublished ->
									qnaViewModel.toggleQnAPublished(
										id = item.id,
										isPublished = isPublished,
										onSuccess = {},
										onError = {}
									)
								}
							)
						}
					}
				}
			}
			
			// Add button for privileged users - always visible at the bottom
			if (isPrivilegedUser) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					horizontalArrangement = Arrangement.Center
				) {
					OutlinedButton(
						onClick = {
							currentQnAItem = null  // Null indicates adding new item
							showAddEditDialog = true
						},
						contentPadding = PaddingValues(
							horizontal = 24.dp,
							vertical = 12.dp
						)
					) {
						Icon(
							Icons.Default.Add,
							contentDescription = "Add Question",
							modifier = Modifier.size(20.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text("Add Question")
					}
				}
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
						onSuccess = {},
						onError = {}
					)
				} else {
					// Update existing item
					qnaViewModel.updateQnAItem(
						id = currentQnAItem!!.id,
						question = question,
						answer = answer,
						isPublished = isPublished,
						onSuccess = {},
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
					onSuccess = {},
					onError = {}
				)
			}
		)
	}
}