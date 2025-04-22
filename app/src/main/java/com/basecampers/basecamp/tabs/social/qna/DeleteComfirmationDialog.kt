package com.basecampers.basecamp.tabs.social.qna

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun DeleteConfirmationDialog(
	onDismiss: () -> Unit,
	onConfirm: () -> Unit
) {
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Delete Item") },
		text = { Text("Are you sure you want to delete this Q&A item? This action cannot be undone.") },
		confirmButton = {
			TextButton(
				onClick = {
					onConfirm()
					onDismiss()
				}
			) {
				Text("Delete", color = MaterialTheme.colorScheme.error)
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancel")
			}
		},
		properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
	)
}