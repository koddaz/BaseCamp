package com.basecampers.basecamp.tabs.social.qna

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.social.models.QnAItem
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.ErrorRed
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary

@Composable
fun QnAItemView(
	item: QnAItem,
	isPrivilegedUser: Boolean,
	onEditClick: () -> Unit,
	onDeleteClick: () -> Unit,
	onPublishToggle: (Boolean) -> Unit
) {
	var isExpanded by remember { mutableStateOf(false) }
	
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		colors = CardDefaults.cardColors(
			containerColor = CardBackground,
			contentColor = TextPrimary
		),
		elevation = CardDefaults.cardElevation(
			defaultElevation = 2.dp
		)
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
		) {
			// Question and Expand/Collapse button
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = item.question,
					style = MaterialTheme.typography.titleMedium.copy(
						fontWeight = FontWeight.Bold
					),
					modifier = Modifier.weight(1f)
				)
				
				IconButton(
					onClick = { isExpanded = !isExpanded }
				) {
					Icon(
						imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
						contentDescription = if (isExpanded) "Collapse" else "Expand",
						tint = SecondaryAqua
					)
				}
			}
			
			// Answer (shown when expanded)
			if (isExpanded) {
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = item.answer,
					style = MaterialTheme.typography.bodyMedium,
					color = TextSecondary
				)
				
				// Admin controls
				if (isPrivilegedUser) {
					Spacer(modifier = Modifier.height(16.dp))
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						// Publish toggle
						Row(
							verticalAlignment = Alignment.CenterVertically
						) {
							Switch(
								checked = item.isPublished,
								onCheckedChange = onPublishToggle,
								colors = SwitchDefaults.colors(
									checkedThumbColor = SecondaryAqua,
									checkedTrackColor = SecondaryAqua.copy(alpha = 0.5f)
								)
							)
							Spacer(modifier = Modifier.width(8.dp))
							Text(
								text = if (item.isPublished) "Published" else "Draft",
								style = MaterialTheme.typography.bodySmall,
								color = if (item.isPublished) TextPrimary else TextSecondary
							)
						}
						
						// Edit and Delete buttons
						Row {
							IconButton(
								onClick = onEditClick
							) {
								Icon(
									Icons.Default.Edit,
									contentDescription = "Edit",
									tint = SecondaryAqua
								)
							}
							IconButton(
								onClick = onDeleteClick
							) {
								Icon(
									Icons.Default.Delete,
									contentDescription = "Delete",
									tint = ErrorRed
								)
							}
						}
					}
				}
			}
		}
	}
}