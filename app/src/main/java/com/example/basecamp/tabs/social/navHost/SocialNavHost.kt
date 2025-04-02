package com.example.basecamp.tabs.social.navHost

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.tabs.social.forum.ForumScreen
import com.example.basecamp.tabs.social.messaging.navHost.MessagingNavHost
import com.example.basecamp.tabs.social.models.socialRoutes
import com.example.basecamp.tabs.social.qna.QnAScreen

@Composable
fun SocialNavHost() {
    val navController = rememberNavController()
    var isSuper by remember { mutableStateOf(false) }
    var unreadCount by remember { mutableStateOf(3) }
    
    // Store the last selected tab to remember when returning to Social tab
    var lastSelectedTab by remember { mutableStateOf(socialRoutes.FORUM) }
    
    NavHost(navController = navController, startDestination = socialRoutes.FORUM) {
        composable(socialRoutes.QNA) {
            QnAScreen(
                onNavigateToQnA = { /* Already on QnA */ },
                onNavigateToForum = {
                    lastSelectedTab = socialRoutes.FORUM
                    navController.navigate(socialRoutes.FORUM)
                },
                onNavigateToMessages = {
                    lastSelectedTab = socialRoutes.MESSAGES
                    navController.navigate(socialRoutes.MESSAGES)
                },
                unreadCount = unreadCount,
                isSuper = isSuper,
                onToggleSuperUser = { isSuper = !isSuper }
            )
        }
        
        composable(socialRoutes.FORUM) {
            ForumScreen(
                onNavigateToQnA = {
                    lastSelectedTab = socialRoutes.QNA
                    navController.navigate(socialRoutes.QNA)
                },
                onNavigateToForum = { /* Already on Forum */ },
                onNavigateToMessages = {
                    lastSelectedTab = socialRoutes.MESSAGES
                    navController.navigate(socialRoutes.MESSAGES)
                },
                unreadCount = unreadCount,
                isSuper = isSuper,
                onToggleSuperUser = { isSuper = !isSuper }
            )
        }
        
        composable(socialRoutes.MESSAGES) {
            MessagingNavHost(
                isSuper = isSuper,
                unreadCount = unreadCount,
                onUnreadCountChanged = { newCount -> unreadCount = newCount },
                onNavigateToQnA = {
                    lastSelectedTab = socialRoutes.QNA
                    navController.navigate(socialRoutes.QNA)
                },
                onNavigateToForum = {
                    lastSelectedTab = socialRoutes.FORUM
                    navController.navigate(socialRoutes.FORUM)
                },
                onNavigateToMessages = { /* Already on Messages */ },
                onToggleSuperUser = { isSuper = !isSuper }
            )
        }
    }
    
    // Effect to navigate to last selected tab when returning to Social tab
    LaunchedEffect(Unit) {
        navController.navigate(lastSelectedTab)
    }
}