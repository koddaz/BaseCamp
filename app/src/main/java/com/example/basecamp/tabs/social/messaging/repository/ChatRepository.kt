package com.example.basecamp.tabs.social.messaging.repository

import com.example.basecamp.tabs.social.messaging.models.Chat
import com.example.basecamp.tabs.social.messaging.models.ChatStatus
import com.example.basecamp.tabs.social.messaging.models.Message
import com.example.basecamp.tabs.social.messaging.models.MessageStatus
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface that handles all chat-related data operations.
 * Serves as the single source of truth by coordinating between Firebase and Room.
 */
interface ChatRepository {
	/**
	 * Get information about a specific chat
	 * @param chatId The unique identifier of the chat
	 * @return Flow of ChatInfo that updates when the chat changes
	 */
	fun getChatInfoById(chatId: String): Flow<ChatInfo?>
	
	/**
	 * Get all chats with a specific status for the current user
	 * @param status The status to filter by (ACTIVE, CLOSED, etc.)
	 * @return Flow of ChatInfo list that updates when chats change
	 */
	fun getChatsByStatus(status: ChatStatus): Flow<List<ChatInfo>>
	
	/**
	 * Get all pending chat requests (for SuperUsers)
	 * @return Flow of pending ChatInfo that updates in real-time
	 */
	fun getPendingChats(): Flow<List<ChatInfo>>
	
	/**
	 * Get chats assigned to the current SuperUser
	 * @return Flow of assigned ChatInfo that updates in real-time
	 */
	fun getAssignedChats(): Flow<List<ChatInfo>>
	
	/**
	 * Get closed chats that were previously assigned to the current SuperUser
	 * @return Flow of closed ChatInfo that updates in real-time
	 */
	fun getSuperUserClosedChats(): Flow<List<ChatInfo>>
	
	/**
	 * Fetch all chats for the current user from Firebase and store in Room
	 */
	suspend fun fetchUserChats()
	
	/**
	 * Fetch all pending chat requests from Firebase and store in Room
	 */
	suspend fun fetchPendingChats()
	
	/**
	 * Fetch all chats assigned to the current SuperUser from Firebase and store in Room
	 */
	suspend fun fetchAssignedChats()
	
	/**
	 * Create a new chat in Firebase
	 * @param chat The chat object to create
	 * @return The ID of the created chat
	 */
	suspend fun createChatInFirebase(chat: Chat): String
	
	/**
	 * Save a chat to the local Room database
	 * @param chat The chat to save
	 */
	suspend fun saveChat(chat: Chat)
	
	/**
	 * Update the status of a chat in Firebase
	 * @param chatId The unique identifier of the chat
	 * @param status The new status
	 */
	suspend fun updateChatStatus(chatId: String, status: ChatStatus)
	
	/**
	 * Update the status of a chat for a specific user
	 * Used for marking chats as deleted for one user but not others
	 * @param chatId The unique identifier of the chat
	 * @param status The new status
	 * @param userId The ID of the user for whom to update the status
	 */
	suspend fun updateChatStatus(chatId: String, status: ChatStatus, userId: String)
	
	/**
	 * Assign a chat to a SuperUser
	 * @param chatId The unique identifier of the chat
	 * @param superUserId The ID of the SuperUser
	 */
	suspend fun assignChatToSuperUser(chatId: String, superUserId: String)
	
	/**
	 * Update the last message text for a chat
	 * @param chatId The unique identifier of the chat
	 * @param lastMessage The new last message text
	 */
	suspend fun updateLastMessage(chatId: String, lastMessage: String)
	
	/**
	 * Delete a chat request from Firebase
	 * Used when a SuperUser declines a request
	 * @param chatId The unique identifier of the chat request
	 */
	suspend fun deleteChatRequest(chatId: String)
	
	/**
	 * Delete a chat from the local Room database only
	 * @param chatId The unique identifier of the chat
	 */
	suspend fun deleteChatLocally(chatId: String)
	
	/**
	 * Get messages for a specific chat from Room
	 * @param chatId The unique identifier of the chat
	 * @return Flow of messages that updates in real-time
	 */
	fun getMessages(chatId: String): Flow<List<Message>>
	
	/**
	 * Get the total number of messages in a chat
	 * @param chatId The unique identifier of the chat
	 * @return The number of messages
	 */
	suspend fun getMessageCount(chatId: String): Int
	
	/**
	 * Fetch the most recent messages for a chat from Firebase and store in Room
	 * @param chatId The unique identifier of the chat
	 * @param count The number of messages to fetch
	 */
	suspend fun fetchRecentMessages(chatId: String, count: Int)
	
	/**
	 * Fetch older messages for pagination
	 * @param chatId The unique identifier of the chat
	 * @param skip The number of messages to skip
	 * @param count The number of messages to fetch
	 * @return The total number of messages now available in Room
	 */
	suspend fun fetchOlderMessages(chatId: String, skip: Int, count: Int): Int
	
	/**
	 * Save a message to the local Room database
	 * @param message The message to save
	 */
	suspend fun saveMessage(message: Message)
	
	/**
	 * Send a message to Firebase
	 * @param message The message to send
	 */
	suspend fun sendMessageToFirebase(message: Message)
	
	/**
	 * Update the status of a message
	 * @param messageId The unique identifier of the message
	 * @param status The new status
	 */
	suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
	
	/**
	 * Update the status of multiple messages
	 * @param messageIds List of message IDs
	 * @param status The new status
	 */
	suspend fun updateMessagesStatus(messageIds: List<String>, status: MessageStatus)
	
	/**
	 * Mark all messages in a chat as read for a user
	 * @param chatId The unique identifier of the chat
	 * @param userId The ID of the user who has read the messages
	 */
	suspend fun markChatAsRead(chatId: String, userId: String)
	
	/**
	 * Update the last read timestamp in Firebase
	 * @param chatId The unique identifier of the chat
	 * @param userId The ID of the user
	 */
	suspend fun updateReadTimestampInFirebase(chatId: String, userId: String)
	
	/**
	 * Get the ID of the current user
	 * @return The user ID
	 */
	fun getCurrentUserId(): String
	
	/**
	 * Get the display name of the current user
	 * @return The user's name
	 */
	fun getCurrentUserName(): String
	
	/**
	 * Check if the current user is a SuperUser
	 * @return True if the user is a SuperUser, false otherwise
	 */
	fun isCurrentUserSuperUser(): Boolean
	
	/**
	 * Get the total number of unread messages across all chats
	 * @return Flow of unread count that updates in real-time
	 */
	fun getTotalUnreadCount(): Flow<Int>
}