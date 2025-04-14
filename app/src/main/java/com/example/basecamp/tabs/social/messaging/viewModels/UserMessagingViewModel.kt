package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.basecamp.tabs.social.messaging.ServiceLocator
import com.example.basecamp.tabs.social.messaging.models.Chat
import com.example.basecamp.tabs.social.messaging.models.ChatStatus
import com.example.basecamp.tabs.social.messaging.models.Message
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.messaging.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for regular users to manage their chats.
 * Handles active/closed chats and creation of new chat requests.
 */
class UserMessagingViewModel @Inject constructor(
	private val chatRepository: ChatRepository
) : ViewModel() {
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading = _isLoading.asStateFlow()
	
	private val _unreadCount = MutableStateFlow(0)
	val unreadCount = _unreadCount.asStateFlow()
	
	init {
		// Load initial data
		refreshChats()
		val chats = getActiveChats()
		// Watch for unread count changes
		viewModelScope.launch {
			chatRepository.getTotalUnreadCount().collect { count ->
				_unreadCount.value = count
			}
		}
	}
	
	class Factory(private val context: android.content.Context) : ViewModelProvider.Factory {
		@Suppress("UNCHECKED_CAST")
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(UserMessagingViewModel::class.java)) {
				return UserMessagingViewModel(
					ServiceLocator.getChatRepository(context)
				) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
	
	/**
	 * Get active chats for the current user
	 * @return Flow of active chats from Room
	 */
	fun getActiveChats(): Flow<List<ChatInfo>> {
		return chatRepository.getChatsByStatus(ChatStatus.ACTIVE)
	}
	
	/**
	 * Get closed chats for the current user
	 * Excludes chats marked as DELETED
	 * @return Flow of closed chats from Room
	 */
	fun getClosedChats(): Flow<List<ChatInfo>> {
		return chatRepository.getChatsByStatus(ChatStatus.CLOSED)
	}
	
	/**
	 * Refresh all chats from Firebase to Room
	 * Updates the local database with the latest chat information
	 */
	fun refreshChats() {
		_isLoading.value = true
		
		viewModelScope.launch {
			try {
				// Log what we're fetching
				println("Fetching all chats for current user from Firebase")
				
				// Fetch from Firebase and update Room
				chatRepository.fetchUserChats()
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	/**
	 * Create a new chat request
	 *
	 * @param subject Topic of the chat
	 * @param initialMessage First message content
	 * @return ID of the newly created chat
	 */
	suspend fun createNewChat(subject: String, initialMessage: String): String {
		_isLoading.value = true
		
		try {
			println("Creating new chat request in Firebase")
			
			val currentUserId = chatRepository.getCurrentUserId()
			val currentUserName = chatRepository.getCurrentUserName()
			
			// Create new chat with PENDING status
			val chat = Chat(
				subject = subject,
				status = ChatStatus.PENDING,
				creatorId = currentUserId,
				lastMessageText = initialMessage
			)
			
			// Create initial message
			val message = Message(
				chatId = chat.id,
				senderId = currentUserId,
				senderName = currentUserName,
				content = initialMessage
			)
			
			// Add system message
			val systemMessage = Message(
				chatId = chat.id,
				senderId = "system",
				senderName = "System",
				content = "Chat started on ${java.text.SimpleDateFormat("MMMM d, yyyy").format(java.util.Date())}",
				isSystemMessage = true
			)
			
			// Save to Firebase
			val chatId = chatRepository.createChatInFirebase(chat)
			chatRepository.sendMessageToFirebase(systemMessage)
			chatRepository.sendMessageToFirebase(message)
			
			// Save to Room
			chatRepository.saveChat(chat)
			chatRepository.saveMessage(systemMessage)
			chatRepository.saveMessage(message)
			
			return chatId
		} finally {
			_isLoading.value = false
		}
	}
	
	/**
	 * Get total unread message count for the current user
	 * @return Flow of integer count, updates in real time
	 */
	fun getUnreadCount(): Flow<Int> {
		return chatRepository.getTotalUnreadCount()
	}
	
	/**
	 * Create a new chat request
	 *
	 * @param subject Topic of the chat
	 * @param message First message content
	 * @return ID of the newly created chat
	 */
	suspend fun createChatRequest(subject: String, message: String): String {
		return createNewChat(subject, message)
	}
}