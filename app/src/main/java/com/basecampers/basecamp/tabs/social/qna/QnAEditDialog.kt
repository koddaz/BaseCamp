package com.basecampers.basecamp.tabs.social.qna

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.basecampers.basecamp.tabs.social.models.QnAItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAEditDialog(
	qnaItem: QnAItem? = null, // Null for new item, non-null for edit
	onDismiss: () -> Unit,
	onSave: (String, String, Boolean) -> Unit
) {
	var question by remember { mutableStateOf(qnaItem?.question ?: "") }
	var answer by remember { mutableStateOf(qnaItem?.answer ?: "") }
	var isPublished by remember { mutableStateOf(qnaItem?.isPublished ?: true) }
	var questionError by remember { mutableStateOf(false) }
	var answerError by remember { mutableStateOf(false) }
	
	Dialog(onDismissRequest = onDismiss) {
		Surface(
			shape = MaterialTheme.shapes.medium,
			color = MaterialTheme.colorScheme.surface
		) {
			Column(
				modifier = Modifier
					.padding(24.dp)
					.fillMaxWidth()
			) {
				Text(
					text = if (qnaItem == null) "Add Q&A Item" else "Edit Q&A Item",
					style = MaterialTheme.typography.headlineSmall
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				OutlinedTextField(
					value = question,
					onValueChange = {
						question = it
						questionError = it.isBlank()
					},
					label = { Text("Question") },
					modifier = Modifier.fillMaxWidth(),
					isError = questionError,
					supportingText = {
						if (questionError) {
							Text("Question cannot be empty")
						}
					}
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				OutlinedTextField(
					value = answer,
					onValueChange = {
						answer = it
						answerError = it.isBlank()
					},
					label = { Text("Answer") },
					modifier = Modifier
						.fillMaxWidth()
						.heightIn(min = 100.dp),
					isError = answerError,
					supportingText = {
						if (answerError) {
							Text("Answer cannot be empty")
						}
					}
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				// Published toggle
				Row(
					modifier = Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically
				) {
					Text("Published")
					Spacer(modifier = Modifier.width(8.dp))
					Switch(
						checked = isPublished,
						onCheckedChange = { isPublished = it }
					)
				}
				
				Spacer(modifier = Modifier.height(24.dp))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(onClick = onDismiss) {
						Text("Cancel")
					}
					
					Spacer(modifier = Modifier.width(8.dp))
					
					Button(
						onClick = {
							if (question.isBlank()) {
								questionError = true
								return@Button
							}
							if (answer.isBlank()) {
								answerError = true
								return@Button
							}
							onSave(question, answer, isPublished)
							onDismiss()
						}
					) {
						Text("Save")
					}
				}
			}
		}
	}
}