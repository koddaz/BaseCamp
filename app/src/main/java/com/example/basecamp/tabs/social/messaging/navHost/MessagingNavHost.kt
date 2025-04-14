package com.example.basecamp.tabs.social.messaging.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.tabs.social.SocialViewModel
import com.example.basecamp.tabs.social.messaging.ChatRequestScreen
import com.example.basecamp.tabs.social.messaging.ChatScreen
import com.example.basecamp.tabs.social.messaging.StartChatScreen
import com.example.basecamp.tabs.social.messaging.SuperUserMessagingScreen
import com.example.basecamp.tabs.social.messaging.UserMessagingScreen
import com.example.basecamp.tabs.social.messaging.models.MessagingRoutes

@Composable
fun MessagingNavHost(
	socialViewModel: SocialViewModel,
	isSuper: Boolean,
	unreadCount: Int
) {
	val navController = rememberNavController()
	
	NavHost(navController = navController, startDestination = MessagingRoutes.MAIN) {
		composable(MessagingRoutes.MAIN) {
			if (isSuper) {
				SuperUserMessagingScreen(
					onSelectPendingChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT_REQUEST}/$chatId")
						// When viewing a chat request, decrement unread count
						socialViewModel.updateUnreadCount(unreadCount - 1)
					},
					onSelectActiveChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId")
					}
				)
			} else {
				UserMessagingScreen(
					onSelectActiveChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId/false")
					},
					onSelectClosedChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId/true")
					},
					onStartNewChat = {
						navController.navigate(MessagingRoutes.START_CHAT)
					}
				)
			}
		}
		
		composable("${MessagingRoutes.CHAT_REQUEST}/{chatId}") { backStackEntry ->
			val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
			ChatRequestScreen(
				chatId = chatId,
				onAccept = {
					navController.navigate("${MessagingRoutes.CHAT}/$chatId") {
						popUpTo(MessagingRoutes.MAIN)
					}
				},
				onDecline = {
					navController.popBackStack()
				}
			)
		}
		
		composable("${MessagingRoutes.CHAT}/{chatId}/{isReadOnly?}") { backStackEntry ->
			val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
			val isReadOnly = backStackEntry.arguments?.getString("isReadOnly")?.toBoolean() ?: false
			
			ChatScreen(
				chatId = chatId,
				isReadOnly = isReadOnly,
				onNavigateBack = {
					navController.popBackStack()
				}
			)
		}
		
		composable(MessagingRoutes.START_CHAT) {
			StartChatScreen(
				onChatStarted = { superUserId ->
					navController.navigate("${MessagingRoutes.CHAT}/$superUserId/false") {
						popUpTo(MessagingRoutes.MAIN)
					}
				},
				onNavigateBack = {
					navController.popBackStack()
				}
			)
		}
	}
}
