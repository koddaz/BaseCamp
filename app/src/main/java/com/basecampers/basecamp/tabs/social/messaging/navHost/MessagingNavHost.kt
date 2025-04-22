package com.basecampers.basecamp.tabs.social.messaging.navHost

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel
import com.basecampers.basecamp.tabs.social.messaging.screens.ChatRequestScreen
import com.basecampers.basecamp.tabs.social.messaging.screens.ChatScreen
import com.basecampers.basecamp.tabs.social.messaging.screens.StartChatScreen
import com.basecampers.basecamp.tabs.social.messaging.screens.SuperUserMessagingScreen
import com.basecampers.basecamp.tabs.social.messaging.screens.UserMessagingScreen
import com.basecampers.basecamp.tabs.social.messaging.models.MessagingRoutes

@Composable
fun MessagingNavHost(
	socialViewModel: SocialViewModel,
	isPrivilegedUser: Boolean,
	unreadCount: Int
) {
	val navController = rememberNavController()
	
	NavHost(navController = navController, startDestination = MessagingRoutes.MAIN) {
		composable(MessagingRoutes.MAIN) {
			if (isPrivilegedUser) {
				SuperUserMessagingScreen(
					onSelectPendingChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT_REQUEST}/$chatId")
					},
					onSelectActiveChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId")
					}
				)
			} else {
				UserMessagingScreen(
					onSelectActiveChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId")
					},
					onSelectClosedChat = { chatId ->
						navController.navigate("${MessagingRoutes.CHAT}/$chatId")
					},
					onStartNewChat = {
						navController.navigate(MessagingRoutes.START_CHAT)
					}
				)
			}
		}
		
		
		composable(route = "${MessagingRoutes.CHAT_REQUEST}/{chatId}") { backStackEntry ->
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
		
		composable(route = "${MessagingRoutes.CHAT}/{chatId}") { backStackEntry ->
			val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
			
			ChatScreen(
				chatId = chatId,
				isReadOnly = false,
				onNavigateBack = {
					navController.popBackStack()
				}
			)
		}
		
		composable(route = MessagingRoutes.START_CHAT) {
			StartChatScreen(
				onChatStarted = { chatId ->
					navController.navigate("${MessagingRoutes.CHAT}/$chatId") {
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