package com.example.basecamp.tabs.social.messaging.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.basecamp.tabs.social.messaging.ServiceLocator
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
 * ViewModel for SuperUsers to manage chat requests and assigned chats.
 * Handles accepting/declining requests and viewing assigned conversations.
 */
class SuperUserMessagingViewModel @Inject constructor(
	private val chatRepository: ChatRepository
) : ViewModel() {
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading = _isLoading.asStateFlow()
	
	init {
		// Load initial data
		refreshChats()
		val activeChats = getAssignedChats()
		val pendingChats = getPendingChats()
	}
	
	class Factory(private val context: android.content.Context) : ViewModelProvider.Factory {
		@Suppress("UNCHECKED_CAST")
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(SuperUserMessagingViewModel::class.java)) {
				return SuperUserMessagingViewModel(
					ServiceLocator.getChatRepository(context)
				) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
	
	/**
	 * Get pending chat requests
	 * Chats with PENDING status that aren't assigned to anyone
	 * @return Flow of pending chat requests from Room
	 */
	fun getPendingChats(): Flow<List<ChatInfo>> {
		return chatRepository.getPendingChats()
	}
	
	/**
	 * Get chats assigned to this SuperUser
	 * @return Flow of assigned chats from Room
	 */
	fun getAssignedChats(): Flow<List<ChatInfo>> {
		return chatRepository.getAssignedChats()
	}
	
	/**
	 * Get closed chats that were assigned to this SuperUser
	 * @return Flow of closed chats from Room
	 */
	fun getClosedChats(): Flow<List<ChatInfo>> {
		return chatRepository.getSuperUserClosedChats()
	}
	
	/**
	 * Refresh all chats from Firebase to Room
	 * Updates the local database with the latest chat information
	 */
	fun refreshChats() {
		_isLoading.value = true
		
		viewModelScope.launch {
			try {
				println("Fetching all pending and assigned chats from Firebase")
				
				// Fetch pending requests
				chatRepository.fetchPendingChats()
				
				// Fetch assigned chats
				chatRepository.fetchAssignedChats()
			} finally {
				_isLoading.value = false
			}
		}
	}
	
	/**
	 * Accept a chat request with initial response
	 *
	 * @param chatId Unique identifier of the chat
	 * @param initialResponse First response message
	 * @return True if successful
	 */
	suspend fun acceptChat(chatId: String, initialResponse: String): Boolean {
		_isLoading.value = true
		
		try {
			println("Accepting chat request in Firebase: $chatId")
			
			val currentUserId = chatRepository.getCurrentUserId()
			val currentUserName = chatRepository.getCurrentUserName()
			
			// Create assignment message
			val assignmentMessage = Message(
				chatId = chatId,
				senderId = "system",
				senderName = "System",
				content = "$currentUserName has been assigned to your chat",
				isSystemMessage = true
			)
			
			// Create initial response
			val responseMessage = Message(
				chatId = chatId,
				senderId = currentUserId,
				senderName = currentUserName,
				content = initialResponse
			)
			
			// Update chat in Firebase
			chatRepository.assignChatToSuperUser(chatId, currentUserId)
			chatRepository.updateChatStatus(chatId, ChatStatus.ACTIVE)
			chatRepository.updateLastMessage(chatId, initialResponse)
			
			// Send messages
			chatRepository.sendMessageToFirebase(assignmentMessage)
			chatRepository.sendMessageToFirebase(responseMessage)
			
			// Update in Room
			chatRepository.saveMessage(assignmentMessage)
			chatRepository.saveMessage(responseMessage)
			
			return true
		} catch (e: Exception) {
			return false
		} finally {
			_isLoading.value = false
		}
	}
	
	/**
	 * Decline a chat request
	 * Removes the request without creating a chat
	 *
	 * @param chatId Unique identifier of the chat request
	 * @return True if successful
	 */
	suspend fun declineChat(chatId: String): Boolean {
		_isLoading.value = true
		
		try {
			println("Declining chat request in Firebase: $chatId")
			
			// Delete the request from Firebase
			chatRepository.deleteChatRequest(chatId)
			
			// Remove from Room
			chatRepository.deleteChatLocally(chatId)
			
			return true
		} catch (e: Exception) {
			return false
		} finally {
			_isLoading.value = false
		}
	}
	
	/**
	 * Get details of a specific chat request
	 * @param chatId Unique identifier of the chat request
	 * @return Flow of ChatInfo for the request
	 */
	fun getChatRequestDetails(chatId: String): Flow<ChatInfo?> {
		return chatRepository.getChatInfoById(chatId)
	}
	
	/**
	 * Accept a chat request with initial response
	 *
	 * @param chatId Unique identifier of the chat
	 * @param responseText First response message
	 */
	fun acceptChatRequestWithResponse(chatId: String, responseText: String) {
		viewModelScope.launch {
			acceptChat(chatId, responseText)
		}
	}
}