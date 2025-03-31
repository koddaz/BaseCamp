package com.example.basecamp.tabs.social

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialScreen(
    onNavigateToQnA: () -> Unit,
    onNavigateToForum: () -> Unit,
    onNavigateToMessages: () -> Unit
) {
    var showNotifications by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Social") },
                actions = {
                    IconButton(onClick = { showNotifications = !showNotifications }) {
                        BadgedBox(
                            badge = {
                                if (!showNotifications) {
                                    Badge { Text("3") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (showNotifications) {
                NotificationSection(
                    onDismiss = { showNotifications = false }
                )
            }
            
            // QnA Preview Section
            SectionPreview(
                title = "Q&A",
                icon = Icons.Default.QuestionAnswer,
                description = "Find answers to common questions",
                actionText = "View All",
                onAction = onNavigateToQnA
            ) {
                // Preview content
                QnAPreview()
            }
            
            // Messages Preview Section
            SectionPreview(
                title = "Messages",
                icon = Icons.Default.Chat,
                description = "Chat with BaseBuddies",
                actionText = "Open Messages",
                onAction = onNavigateToMessages,
                badge = "2"
            ) {
                // Preview content
                MessagesPreview()
            }
            
            // Forum Preview Section
            SectionPreview(
                title = "Forum",
                icon = Icons.Default.Forum,
                description = "Discuss and share with the community",
                actionText = "Go to Forum",
                onAction = onNavigateToForum
            ) {
                // Preview content
                ForumPreview()
            }
        }
    }
}

@Composable
fun NotificationSection(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close notifications"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Sample notifications
            NotificationItem(
                title = "New answer in Forum",
                description = "Your question about booking has a new response",
                time = "5 min ago",
                isNew = true,
                onItemClick = {}
            )
            
            NotificationItem(
                title = "BaseBuddy chat ended",
                description = "Your chat with John has been closed",
                time = "1 hour ago",
                isNew = true,
                onItemClick = {}
            )
            
            NotificationItem(
                title = "New Q&A published",
                description = "Check out updated facility information",
                time = "Yesterday",
                isNew = false,
                onItemClick = {}
            )
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    description: String,
    time: String,
    isNew: Boolean,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onItemClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isNew) MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isNew) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionPreview(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    actionText: String,
    badge: String? = null,
    onAction: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = onAction
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge {
                        Text(badge)
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                TextButton(onClick = onAction) {
                    Text(actionText)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
fun QnAPreview() {
    Column {
        QnAItem(
            question = "How do I book a training session?",
            answer = "You can book training sessions through the Booking tab or from the facility page.",
            isExpanded = true
        )
        Divider()
        QnAItem(
            question = "What is the cancellation policy?",
            answer = "You can cancel bookings up to 24 hours before the scheduled time without penalty.",
            isExpanded = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnAItem(
    question: String,
    answer: String,
    isExpanded: Boolean
) {
    var expanded by remember { mutableStateOf(isExpanded) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MessagesPreview() {
    Column {
        MessageItem(
            name = "John (BaseBuddy)",
            message = "I've checked the booking system and your session is confirmed",
            time = "12:30 PM",
            isNew = true
        )
        
        MessageItem(
            name = "Support",
            message = "Your chat request is waiting for a BaseBuddy to accept",
            time = "Yesterday",
            isNew = false
        )
    }
}

@Composable
fun MessageItem(
    name: String,
    message: String,
    time: String,
    isNew: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                if (isNew) {
                    Badge {
                        Text("New")
                    }
                }
            }
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
        
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ForumPreview() {
    Column {
        ForumPostItem(
            title = "App crashes during booking",
            category = "App Functionality",
            author = "Mike",
            timePosted = "2 hours ago",
            replies = 5,
            hasOfficialReply = true
        )
        
        ForumPostItem(
            title = "Maintenance schedule for pool area",
            category = "Facility Maintenance",
            author = "Sarah (BaseBuddy)",
            timePosted = "Yesterday",
            replies = 12,
            hasOfficialReply = true
        )
    }
}

@Composable
fun ForumPostItem(
    title: String,
    category: String,
    author: String,
    timePosted: String,
    replies: Int,
    hasOfficialReply: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                if (hasOfficialReply) {
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
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By $author",
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
                    text = timePosted,
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
                    text = replies.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/*
Social (Overview Page)

QnA Section Preview

Featured/recent Q&A items with official answers
"View All" button to navigate to full QnA page


Messages Preview

Active chats or new message requests
Notification badge for unread messages
"Open Messages" button


Forum Highlights

Recent or trending forum posts
Category filters preview
"Go to Forum" button


Notification Center (available throughout Social tab)

Expandable section showing recent notifications
Click/tap navigates user to relevant view
Unread indicator


-------

QnA Section

User View

Search bar at the top
List of questions with expandable answers
Questions grouped by categories/topics
Read more/less functionality for longer answers


Admin/BaseBuddy View

All user view features
Edit button for questions and answers
Add new Q&A entry option
Hide/remove functionality
Ability to pin important Q&As to top

-------

Private Messaging (PM)

Active Chats

List of ongoing conversations with BaseBuddies
Status indicator (active/closed)
Last message preview and timestamp
Toggle to hide/show closed chats


New Chat Request

Button to initiate a new BaseBuddy chat
Description of what BaseBuddies can help with


BaseBuddy Controls (for BB users)

Accept/decline chat requests
Transfer chat to another BB option
Close chat functionality

-------

Forum

Search Interface

Search field with suggested/trending topics
Category filters:

Booking Issue
App Functionality
Facility Maintenance
Error Report


Search results sorting:

Most recent
Most commented
Unanswered



Dynamic Results

Posts matching search terms/filters
Option to view more results


Post Creation

"Create Post" button (always visible)
When pressed:

Title field (auto-populated with search term if any)
Content text field
Category selection
Submit button


-------

Post View

Official replies (BaseBuddy/Admin) pinned at top
Regular user comments below
BB/Admin indicators next to names
Moderation options for BB/Admin users
Up/down voting for forum answers
Sorting options within post:

Highest voted
Most recent

*/

