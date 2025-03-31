package com.example.basecamp.tabs.social.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.tabs.social.SocialScreen
import com.example.basecamp.tabs.social.forum.ForumScreen
import com.example.basecamp.tabs.social.messaging.MessagingScreen
import com.example.basecamp.tabs.social.models.socialRoutes
import com.example.basecamp.tabs.social.qna.QnAScreen

@Composable
fun SocialNavHost() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = socialRoutes.MAIN) {
        composable(socialRoutes.MAIN) {
            SocialScreen(
                onNavigateToQnA = { navController.navigate(socialRoutes.QNA) },
                onNavigateToForum = { navController.navigate(socialRoutes.FORUM) },
                onNavigateToMessages = { navController.navigate(socialRoutes.MESSAGES) }
            )
        }
        composable(socialRoutes.QNA) {
            QnAScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(socialRoutes.FORUM) {
            ForumScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(socialRoutes.MESSAGES) {
            MessagingScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}