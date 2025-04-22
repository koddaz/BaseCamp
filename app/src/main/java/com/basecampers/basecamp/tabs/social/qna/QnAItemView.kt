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
@Composable
fun QnAItemView(
	item: QnAItem,
	isPrivilegedUser: Boolean,
	onEditClick: () -> Unit,
	onDeleteClick: () -> Unit,
	onPublishToggle: (Boolean) -> Unit
) {
	var expanded by remember { mutableStateOf(false) }
	
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 8.dp),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			// Question row with expand/collapse control
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.clickable { expanded = !expanded },
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				Text(
					text = item.question,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.weight(1f)
				)
				
				if (!item.isPublished && isPrivilegedUser) {
					// Show draft indicator for privileged users
					Text(
						text = "DRAFT",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.error,
						modifier = Modifier.padding(end = 8.dp)
					)
				}
				
				Icon(
					imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
					contentDescription = if (expanded) "Collapse" else "Expand"
				)
			}
			
			// Answer section (expandable)
			AnimatedVisibility(
				visible = expanded,
				enter = expandVertically(),
				exit = shrinkVertically()
			) {
				Column {
					Divider(modifier = Modifier.padding(vertical = 8.dp))
					Text(
						text = item.answer,
						style = MaterialTheme.typography.bodyMedium
					)
					
					// Edit/Delete controls for privileged users
					if (isPrivilegedUser) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(top = 16.dp),
							horizontalArrangement = Arrangement.End,
							verticalAlignment = Alignment.CenterVertically
						) {
							// Published toggle
							Row(
								verticalAlignment = Alignment.CenterVertically
							) {
								Text("Published")
								Switch(
									checked = item.isPublished,
									onCheckedChange = onPublishToggle,
									modifier = Modifier.padding(horizontal = 8.dp)
								)
							}
							
							Spacer(modifier = Modifier.width(16.dp))
							
							// Edit button
							IconButton(onClick = onEditClick) {
								Icon(
									imageVector = Icons.Default.Edit,
									contentDescription = "Edit"
								)
							}
							
							// Delete button
							IconButton(onClick = onDeleteClick) {
								Icon(
									imageVector = Icons.Default.Delete,
									contentDescription = "Delete",
									tint = MaterialTheme.colorScheme.error
								)
							}
						}
					}
				}
			}
		}
	}
}