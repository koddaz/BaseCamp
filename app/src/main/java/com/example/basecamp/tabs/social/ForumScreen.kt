package com.example.basecamp.tabs.social.forum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
	onNavigateBack: () -> Unit
) {
	val forumPosts = remember {
		listOf(
			ForumPost(
				id = "1",
				title = "App crashes during booking",
				content = "I've been trying to book a session for the past hour but the app keeps crashing. Anyone else experiencing this?",
				author = "Mike",
				category = "App Functionality",
				timePosted = "2 hours ago",
				replies = 5,
				hasOfficialReply = true
			),
			ForumPost(
				id = "2",
				title = "Maintenance schedule for pool area",
				content = "Is there a published maintenance schedule for the pool area? I've noticed it's been closed several times recently.",
				author = "Sarah (BaseBuddy)",
				category = "Facility Maintenance",
				timePosted = "Yesterday",
				replies = 12,
				hasOfficialReply = true
			),
			ForumPost(
				id = "3",
				title = "Group booking discount",
				content = "Is there a discount for group bookings? I'd like to bring my team for a day of activities.",
				author = "Robert",
				category = "Booking Issue",
				timePosted = "2 days ago",
				replies = 3,
				hasOfficialReply = false
			),
			ForumPost(
				id = "4",
				title = "Error when trying to update profile picture",
				content = "I keep getting an error when trying to update my profile picture. The error says 'File size too large' but the image is only 2MB.",
				author = "Jennifer",
				category = "Error Report",
				timePosted = "3 days ago",
				replies = 0,
				hasOfficialReply = false
			)
		)
	}
	
	var searchQuery by remember { mutableStateOf("") }
	var showCreatePost by remember { mutableStateOf(false) }
	var selectedCategory by remember { mutableStateOf<String?>(null) }
	var sortOption by remember { mutableStateOf("Most Recent") }
	
	val categories = listOf("Booking Issue", "App Functionality", "Facility Maintenance", "Error Report")
	val sortOptions = listOf("Most Recent", "Most Commented", "Unanswered")
	
	var filteredPosts by remember { mutableStateOf(forumPosts) }
	
	LaunchedEffect(searchQuery, selectedCategory, sortOption) {
		var tempFiltered = forumPosts
		
		// Apply search filter
		if (searchQuery.isNotBlank()) {
			tempFiltered = tempFiltered.filter {
				it.title.contains(searchQuery, ignoreCase = true) ||
						it.content.contains(searchQuery, ignoreCase = true)
			}
		}
		
		// Apply category filter
		if (selectedCategory != null) {
			tempFiltered = tempFiltered.filter { it.category == selectedCategory }
		}
		
		// Apply sorting
		tempFiltered = when (sortOption) {
			"Most Recent" -> tempFiltered.sortedByDescending { it.timePosted }
			"Most Commented" -> tempFiltered.sortedByDescending { it.replies }
			"Unanswered" -> tempFiltered.filter { it.replies == 0 }
			else -> tempFiltered
		}
		
		filteredPosts = tempFiltered
	}
	
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Forum") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(
							imageVector = Icons.Default.ArrowBack,
							contentDescription = "Back"
						)
					}
				}
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = { showCreatePost = true }
			) {
				Icon(Icons.Default.Add, contentDescription = "Create Post")
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
		) {
			// Search and Filter Section
			Card(
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					// Search field
					OutlinedTextField(
						value = searchQuery,
						onValueChange = { searchQuery = it },
						modifier = Modifier.fillMaxWidth(),
						placeholder = { Text("Search in forum...") },
						leadingIcon = {
							Icon(
								imageVector = Icons.Default.Search,
								contentDescription = "Search"
							)
						},
						singleLine = true
					)
					
					Spacer(modifier = Modifier.height(16.dp))
					
					// Category filters
					Text(
						text = "Filter by Category",
						style = MaterialTheme.typography.labelMedium,
						fontWeight = FontWeight.Bold
					)
					
					Spacer(modifier = Modifier.height(8.dp))
					
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						categories.forEach { category ->
							FilterChip(
								selected = category == selectedCategory,
								onClick = {
									selectedCategory = if (category == selectedCategory) null else category
								},
								label = { Text(category) }
							)
						}
					}
					
					Spacer(modifier = Modifier.height(16.dp))
					
					// Sort options
					Row(
						verticalAlignment = Alignment.CenterVertically
					) {
						Text(
							text = "Sort by:",
							style = MaterialTheme.typography.labelMedium,
							fontWeight = FontWeight.Bold
						)
						
						Spacer(modifier = Modifier.width(8.dp))
						
						var expanded by remember { mutableStateOf(false) }
						
						ExposedDropdownMenuBox(
							expanded = expanded,
							onExpandedChange = { expanded = !expanded }
						) {
							TextField(
								value = sortOption,
								onValueChange = {},
								readOnly = true,
								trailingIcon = {
									ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
								},
								colors = ExposedDropdownMenuDefaults.textFieldColors(),
								modifier = Modifier.menuAnchor()
							)
							
							ExposedDropdownMenu(
								expanded = expanded,
								onDismissRequest = { expanded = false }
							) {
								sortOptions.forEach { option ->
									DropdownMenuItem(
										text = { Text(option) },
										onClick = {
											sortOption = option
											expanded = false
										}
									)
								}
							}
						}
					}
				}
			}
			
			// Posts List
			LazyColumn {
				items(filteredPosts) { post ->
					ForumPostCard(post = post)
				}
				
				item {
					Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
				}
			}
		}
		
		// Create Post Dialog
		if (showCreatePost) {
			CreatePostDialog(
				searchQuery = searchQuery,
				onDismiss = { showCreatePost = false },
				onCreatePost = { title, content, category ->
					// Handle post creation
					showCreatePost = false
				}
			)
		}
	}
}

@Composable
fun ForumPostCard(post: ForumPost) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp, vertical = 8.dp),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface
		)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = post.category,
					style = MaterialTheme.typography.labelMedium,
					color = MaterialTheme.colorScheme.primary,
					modifier = Modifier
						.clip(RoundedCornerShape(4.dp))
						.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
						.padding(horizontal = 8.dp, vertical = 2.dp)
				)
				
				Spacer(modifier = Modifier.width(8.dp))
				
				if (post.hasOfficialReply) {
					Icon(
						imageVector = Icons.Default.Verified,
						contentDescription = "Official reply",
						tint = MaterialTheme.colorScheme.tertiary,
						modifier = Modifier.size(16.dp)
					)
					
					Spacer(modifier = Modifier.width(4.dp))
					
					Text(
						text = "Official Reply",
						style = MaterialTheme.typography.labelSmall,
						color = MaterialTheme.colorScheme.tertiary
					)
				}
			}
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = post.title,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Text(
				text = post.content,
				style = MaterialTheme.typography.bodyMedium,
				maxLines = 3
			)
			
			Spacer(modifier = Modifier.height(8.dp))
			
			Row(
				modifier = Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "By ${post.author}",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				
				Spacer(modifier = Modifier.width(8.dp))
				
				Text(
					text = "•",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				
				Spacer(modifier = Modifier.width(8.dp))
				
				Text(
					text = post.timePosted,
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
				
				Spacer(modifier = Modifier.weight(1f))
				
				Icon(
					imageVector = Icons.Default.Comment,
					contentDescription = "Replies",
					modifier = Modifier.size(16.dp),
					tint = MaterialTheme.colorScheme.onSurfaceVariant
				)
				
				Spacer(modifier = Modifier.width(4.dp))
				
				Text(
					text = post.replies.toString(),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostDialog(
	searchQuery: String,
	onDismiss: () -> Unit,
	onCreatePost: (title: String, content: String, category: String) -> Unit
) {
	var title by remember { mutableStateOf(searchQuery) }
	var content by remember { mutableStateOf("") }
	var selectedCategory by remember { mutableStateOf("App Functionality") }
	
	val categories = listOf("Booking Issue", "App Functionality", "Facility Maintenance", "Error Report")
	
	AlertDialog(
		onDismissRequest = onDismiss,
		title = { Text("Create New Post") },
		text = {
			Column {
				OutlinedTextField(
					value = title,
					onValueChange = { title = it },
					label = { Text("Title") },
					modifier = Modifier.fillMaxWidth()
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				OutlinedTextField(
					value = content,
					onValueChange = { content = it },
					label = { Text("Content") },
					modifier = Modifier
						.fillMaxWidth()
						.height(120.dp),
					maxLines = 5
				)
				
				Spacer(modifier = Modifier.height(16.dp))
				
				Text(
					text = "Category",
					style = MaterialTheme.typography.labelMedium,
					fontWeight = FontWeight.Bold
				)
				
				Spacer(modifier = Modifier.height(8.dp))
				
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					categories.forEach { category ->
						FilterChip(
							selected = category == selectedCategory,
							onClick = { selectedCategory = category },
							label = { Text(category) }
						)
					}
				}
			}
		},
		confirmButton = {
			Button(
				onClick = { onCreatePost(title, content, selectedCategory) },
				enabled = title.isNotBlank() && content.isNotBlank()
			) {
				Text("Post")
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text("Cancel")
			}
		}
	)
}

data class ForumPost(
	val id: String,
	val title: String,
	val content: String,
	val author: String,
	val category: String,
	val timePosted: String,
	val replies: Int,
	val hasOfficialReply: Boolean
)