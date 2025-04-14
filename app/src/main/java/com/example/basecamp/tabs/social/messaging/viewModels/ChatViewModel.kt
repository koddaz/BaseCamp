package com.example.basecamp.tabs.social.messaging.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.basecamp.tabs.social.messaging.ServiceLocator
import com.example.basecamp.tabs.social.messaging.models.Message
import com.example.basecamp.tabs.social.messaging.models.MessageStatus
import com.example.basecamp.tabs.social.messaging.models.ChatStatus
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.messaging.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing a specific chat conversation.
 * Handles messages, read status, and chat lifecycle.
 */
class ChatViewModel @Inject constructor(
	private val chatRepository: ChatRepository
) : ViewModel() {
	
	private val _isLoading = MutableStateFlow(false)
	val isLoading = _isLoading.asStateFlow()
	
	private val _messageCount = MutableStateFlow(0)
	val messageCount = _messageCount.asStateFlow()
	
	class Factory(private val context: Context) : ViewModelProvider.Factory {
		@Suppress("UNCHECKED_CAST")
		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
				return ChatViewModel(
					ServiceLocator.getChatRepository(context)
				) as T
			}
			throw IllegalArgumentException("Unknown ViewModel class")
		}
	}
	
	/**
	 * Get chat information
	 *
	 * @param chatId Unique identifier of the chat
	 * @return Flow of ChatInfo for the specified chat
	 */
	fun getChatInfo(chatId: String): Flow<ChatInfo?> {
		return chatRepository.getChatInfoById(chatId)
	}
	
	/**
	 * Get messages for a specific chat
	 * Initially loads the 10 most recent messages
	 *
	 * @param chatId Unique identifier of the chat
	 * @return Flow of message list, updated as new messages arrive
	 */
	fun getMessages(chatId: String): Flow<List<Message>> {
		_isLoading.value = true
		
		viewModelScope.launch {
			try {
				// Fetch latest messages from Firebase and store in Room
				chatRepository.fetchRecentMessages(chatId, 10)
				
				// Update the message count for pagination
				_messageCount.value = chatRepository.getMessageCount(chatId)
			} finally {
				_isLoading.value = false
			}
		}
		
		// Return messages from Room as the source of truth
		return chatRepository.getMessages(chatId)
	}
	
	/**
	 * Load more messages (for pagination)
	 * Loads an additional 10 older messages from the current position
	 *
	 * @param chatId Unique identifier of the chat
	 * @return True if more messages were loaded, false if all messages are already loaded
	 */
	suspend fun loadMoreMessages(chatId: String): Boolean {
		_isLoading.value = true
		
		try {
			val currentCount = _messageCount.value
			val newCount = chatRepository.fetchOlderMessages(chatId, currentCount, 10)
			_messageCount.value = newCount
			
			// Return true if we loaded more messages
			return newCount > currentCount
		} finally {
			_isLoading.value = false
		}
	}
	
	/**
	 * Send a new message in the chat
	 *
	 * @param chatId Unique identifier of the chat
	 * @param content Text content of the message
	 */
	fun sendMessage(chatId: String, content: String) {
		viewModelScope.launch {
			val currentUserId = chatRepository.getCurrentUserId()
			val currentUserName = chatRepository.getCurrentUserName()
			
			// Create message with SENDING status initially
			val message = Message(
				chatId = chatId,
				senderId = currentUserId,
				senderName = currentUserName,
				content = content,
				status = MessageStatus.SENDING
			)
			
			// Save to Room immediately for responsive UI
			chatRepository.saveMessage(message)
			
			// Try to send to Firebase
			try {
				chatRepository.sendMessageToFirebase(message)
				// Update status to SENT in Room
				chatRepository.updateMessageStatus(message.id, MessageStatus.SENT)
			} catch (e: Exception) {
				// Handle failure
				// Keep status as SENDING to retry later
				// Log error
			}
		}
	}
	
	/**
	 * Check if a chat is pending (awaiting SuperUser response)
	 *
	 * @param chatId Unique identifier of the chat
	 * @return Flow of boolean indicating if chat is pending
	 */
	fun isPendingChat(chatId: String): Flow<Boolean> {
		return chatRepository.getChatInfoById(chatId).map { chatInfo ->
			chatInfo?.status == ChatStatus.PENDING && chatInfo.assignedToId == null
		}
	}
	
	/**
	 * Mark messages as received when they are downloaded to Room
	 * Should be called internally by the repository when new messages are synced
	 *
	 * @param messageIds List of message IDs to mark as received
	 */
	fun markAsReceived(messageIds: List<String>) {
		viewModelScope.launch {
			chatRepository.updateMessagesStatus(messageIds, MessageStatus.DELIVERED)
		}
	}
	
	/**
	 * Mark all unread messages in a chat as read
	 * Called when the user opens and views the chat
	 *
	 * @param chatId Unique identifier of the chat
	 */
	fun markAsRead(chatId: String) {
		viewModelScope.launch {
			val currentUserId = chatRepository.getCurrentUserId()
			
			// Update messages in Room
			chatRepository.markChatAsRead(chatId, currentUserId)
			
			// Sync read status to Firebase
			chatRepository.updateReadTimestampInFirebase(chatId, currentUserId)
		}
	}
	
	/**
	 * Close a chat (SuperUser only)
	 * Changes status to CLOSED, preventing new messages
	 *
	 * @param chatId Unique identifier of the chat
	 * @return True if successful, false if user doesn't have permission
	 */
	suspend fun closeChat(chatId: String): Boolean {
		val currentUserId = chatRepository.getCurrentUserId()
		val isSuperUser = chatRepository.isCurrentUserSuperUser()
		
		// Only SuperUsers can close chats
		if (!isSuperUser) {
			return false
		}
		
		return try {
			// Add system message about closing
			val systemMessage = Message(
				chatId = chatId,
				senderId = "system",
				senderName = "System",
				content = "This chat has been closed",
				isSystemMessage = true
			)
			
			// Update in Firebase
			chatRepository.updateChatStatus(chatId, ChatStatus.CLOSED)
			chatRepository.sendMessageToFirebase(systemMessage)
			
			// Update in Room
			chatRepository.saveMessage(systemMessage)
			true
		} catch (e: Exception) {
			false
		}
	}
	
	/**
	 * Mark a chat as deleted (User only)
	 * Hidden from user but still visible to SuperUsers
	 *
	 * @param chatId Unique identifier of the chat
	 * @return True if successful, false if not permitted
	 */
	suspend fun deleteChat(chatId: String): Boolean {
		val currentUserId = chatRepository.getCurrentUserId()
		val isSuperUser = chatRepository.isCurrentUserSuperUser()
		
		// Only regular users can delete chats
		if (isSuperUser) {
			return false
		}
		
		return try {
			// Update in Firebase - only changes visibility for this user
			chatRepository.updateChatStatus(chatId, ChatStatus.DELETED, currentUserId)
			
			// Update in Room
			true
		} catch (e: Exception) {
			false
		}
	}
	
	/**
	 * Get the current user ID
	 */
	fun getCurrentUserId(): String {
		return chatRepository.getCurrentUserId()
	}
}