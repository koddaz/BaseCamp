package com.example.basecamp.tabs.social.navHost

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.ui.theme.BaseCampTheme
import com.example.basecamp.tabs.social.SocialViewModel
import com.example.basecamp.tabs.social.forum.ForumScreen
import com.example.basecamp.tabs.social.messaging.navHost.MessagingNavHost
import com.example.basecamp.tabs.social.qna.QnAScreen

@Composable
fun SocialNavHost(
    socialViewModel: SocialViewModel,
    selectedSocialTabIndex: Int = 1,
    onSocialTabSelected: (Int) -> Unit = {}
) {
    // Get states from ViewModel
    val isSuper by socialViewModel.isSuper.collectAsState()
    val unreadCount by socialViewModel.unreadCount.collectAsState()
    
    // Local copy of the selectedSocialTabIndex
    var currentSocialTabIndex by remember { mutableIntStateOf(selectedSocialTabIndex) }
    
    // Update current tab when external state changes
    LaunchedEffect(selectedSocialTabIndex) {
        currentSocialTabIndex = selectedSocialTabIndex
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Add top padding for safe area (temporary solution)
        Spacer(modifier = Modifier.height(25.dp))
        
        // SuperUser toggle (discreetly placed at the top)
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { socialViewModel.toggleSuperUser() }
            ) {
                Text(if (isSuper) "SuperUser Mode" else "User Mode")
            }
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isSuper,
                onCheckedChange = { socialViewModel.toggleSuperUser() }
            )
        }
        
        // Content area
        Column(modifier = Modifier.fillMaxSize()) {
            
            Column(modifier = Modifier.weight(1f)) {
                when (currentSocialTabIndex) {
                    0 -> QnAScreen(
                        isSuper = isSuper,
                        onToggleSuperUser = { socialViewModel.toggleSuperUser() }
                    )
                    1 -> ForumScreen(
                        isSuper = isSuper,
                        onToggleSuperUser = { socialViewModel.toggleSuperUser() }
                    )
                    2 ->  MessagingNavHost(
                        socialViewModel = socialViewModel,
                        isSuper = isSuper,
                        unreadCount = unreadCount
                    )
                    else -> Text("Error: Social tab not found")
                }
            }
            
            // Top navigation bar for social tabs
            NavigationBar {
                val socialTabs = listOf(
                    SocialTabItem("Q&A", Icons.Filled.QuestionAnswer, Icons.Outlined.QuestionAnswer),
                    SocialTabItem("Forum", Icons.Filled.Forum, Icons.Outlined.Forum),
                    SocialTabItem(
                        "Messages${if (unreadCount > 0) " ($unreadCount)" else ""}",
                        Icons.Filled.Message,
                        Icons.Outlined.Message
                    )
                )
                
                socialTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (currentSocialTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = currentSocialTabIndex == index,
                        onClick = {
                            currentSocialTabIndex = index
                            onSocialTabSelected(index)
                        }
                    )
                }
            }
        }
    }
}

private data class SocialTabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun SocialNavHostPreview() {
    BaseCampTheme {
        SocialNavHost(socialViewModel = viewModel())
    }
}