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
import com.example.basecamp.tabs.social.messaging.models.messagingRoutes

@Composable
fun MessagingNavHost(
	socialViewModel: SocialViewModel,
	isSuper: Boolean,
	unreadCount: Int
) {
	val navController = rememberNavController()
	
	NavHost(navController = navController, startDestination = messagingRoutes.MAIN) {
		composable(messagingRoutes.MAIN) {
			if (isSuper) {
				SuperUserMessagingScreen(
					onSelectPendingChat = { chatId ->
						navController.navigate("${messagingRoutes.CHAT_REQUEST}/$chatId")
						// When viewing a chat request, decrement unread count
						socialViewModel.updateUnreadCount(unreadCount - 1)
					},
					onSelectActiveChat = { chatId ->
						navController.navigate("${messagingRoutes.CHAT}/$chatId")
					}
				)
			} else {
				UserMessagingScreen(
					onSelectActiveChat = { chatId ->
						navController.navigate("${messagingRoutes.CHAT}/$chatId/false")
					},
					onSelectClosedChat = { chatId ->
						navController.navigate("${messagingRoutes.CHAT}/$chatId/true")
					},
					onStartNewChat = {
						navController.navigate(messagingRoutes.START_CHAT)
					}
				)
			}
		}
		
		composable("${messagingRoutes.CHAT_REQUEST}/{chatId}") { backStackEntry ->
			val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
			ChatRequestScreen(
				chatId = chatId,
				onAccept = {
					navController.navigate("${messagingRoutes.CHAT}/$chatId") {
						popUpTo(messagingRoutes.MAIN)
					}
				},
				onDecline = {
					navController.popBackStack()
				}
			)
		}
		
		composable("${messagingRoutes.CHAT}/{chatId}/{isReadOnly?}") { backStackEntry ->
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
		
		composable(messagingRoutes.START_CHAT) {
			StartChatScreen(
				onChatStarted = { superUserId ->
					navController.navigate("${messagingRoutes.CHAT}/$superUserId/false") {
						popUpTo(messagingRoutes.MAIN)
					}
				},
				onNavigateBack = {
					navController.popBackStack()
				}
			)
		}
	}
}