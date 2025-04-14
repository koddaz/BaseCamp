package com.example.basecamp.tabs.social.messaging.repository

import android.util.Log
import com.example.basecamp.tabs.social.messaging.db.dao.ChatInfoDao
import com.example.basecamp.tabs.social.messaging.db.dao.ChatParticipantDao
import com.example.basecamp.tabs.social.messaging.db.dao.MessageDao
import com.example.basecamp.tabs.social.messaging.db.entity.ChatInfoEntity
import com.example.basecamp.tabs.social.messaging.db.entity.ChatParticipantEntity
import com.example.basecamp.tabs.social.messaging.db.entity.MessageEntity
import com.example.basecamp.tabs.social.messaging.models.Chat
import com.example.basecamp.tabs.social.messaging.models.ChatStatus
import com.example.basecamp.tabs.social.messaging.models.Message
import com.example.basecamp.tabs.social.messaging.models.MessageStatus
import com.example.basecamp.tabs.social.messaging.models.Participant
import com.example.basecamp.tabs.social.messaging.models.room.ChatInfo
import com.example.basecamp.tabs.social.models.UserRole
import com.example.basecamp.tabs.social.session.UserSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ChatRepository"

/**
 * Implementation of ChatRepository that coordinates between
 * Firebase Firestore and Room database.
 */
@Singleton
class ChatRepositoryImpl @Inject constructor(
	private val firestore: FirebaseFirestore,
	private val chatInfoDao: ChatInfoDao,
	private val participantDao: ChatParticipantDao,
	private val messageDao: MessageDao,
	private val userSession: UserSession
) : ChatRepository {
	
	/**
	 * Get information about a specific chat
	 */
	override fun getChatInfoById(chatId: String): Flow<ChatInfo?> {
		Log.d(TAG, "Getting chat info for chat: $chatId")
		return chatInfoDao.getChatById(chatId).map { entity ->
			entity?.toChatInfo()
		}
	}
	
	/**
	 * Get all chats with a specific status for the current user
	 */
	override fun getChatsByStatus(status: ChatStatus): Flow<List<ChatInfo>> {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Getting chats with status: $status for user: $userId in company: $companyId")
		
		return chatInfoDao.getChatsByStatusAndParticipant(
			status = status.name,
			userId = userId,
			companyId = companyId
		).map { entities ->
			entities.map { it.toChatInfo() }
		}
	}
	
	/**
	 * Get all pending chat requests (for SuperUsers)
	 */
	override fun getPendingChats(): Flow<List<ChatInfo>> {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Getting pending chats for company: $companyId")
		
		return chatInfoDao.getPendingChats(companyId).map { entities ->
			entities.map { it.toChatInfo() }
		}
	}
	
	/**
	 * Get chats assigned to the current SuperUser
	 */
	override fun getAssignedChats(): Flow<List<ChatInfo>> {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Getting assigned chats for SuperUser: $userId in company: $companyId")
		
		return chatInfoDao.getAssignedChats(userId, companyId).map { entities ->
			entities.map { it.toChatInfo() }
		}
	}
	
	/**
	 * Get closed chats that were previously assigned to the current SuperUser
	 */
	override fun getSuperUserClosedChats(): Flow<List<ChatInfo>> {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Getting closed chats for SuperUser: $userId in company: $companyId")
		
		return chatInfoDao.getClosedAssignedChats(userId, companyId).map { entities ->
			entities.map { it.toChatInfo() }
		}
	}
	
	/**
	 * Fetch all chats for the current user from Firebase and store in Room
	 */
	override suspend fun fetchUserChats() {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Fetching all chats for user: $userId in company: $companyId")
		
		try {
			// Query chats where the user is a participant
			val chatSnapshots = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.whereArrayContains("participants.userIds", userId)
				.get()
				.await()
			
			// Process each chat
			for (chatDoc in chatSnapshots.documents) {
				val chat = chatDoc.toObject(Chat::class.java) ?: continue
				
				// Save chat to Room
				val title = generateChatTitle(chat)
				val chatEntity = ChatInfoEntity(
					id = chat.id,
					companyId = companyId,
					title = title,
					status = chat.status.name,
					createdAt = chat.createdAt,
					lastMessageTime = chat.lastMessageTime,
					lastMessageText = chat.lastMessageText,
					creatorId = chat.creatorId,
					assignedToId = chat.assignedToId,
					subject = chat.subject,
					unreadCount = getUnreadCountForUser(chat, userId)
				)
				chatInfoDao.insertChat(chatEntity)
				
				// Save participants
				for (participant in chat.participants) {
					val participantEntity = ChatParticipantEntity(
						chatId = chat.id,
						userId = participant.userId,
						displayName = participant.displayName,
						role = participant.role.name,
						unreadCount = participant.unreadCount,
						lastReadTimestamp = participant.lastReadTimestamp
					)
					participantDao.insertParticipant(participantEntity)
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error fetching user chats", e)
			throw e
		}
	}
	
	/**
	 * Fetch all pending chat requests from Firebase and store in Room
	 */
	override suspend fun fetchPendingChats() {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Fetching pending chats for company: $companyId")
		
		try {
			// Query pending chats
			val chatSnapshots = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.whereEqualTo("status", ChatStatus.PENDING.name)
				.whereEqualTo("assignedToId", null)
				.get()
				.await()
			
			// Process each chat
			for (chatDoc in chatSnapshots.documents) {
				val chat = chatDoc.toObject(Chat::class.java) ?: continue
				
				// Generate title for pending chats
				val title = "Request from ${getCreatorName(chat)}"
				
				// Save chat to Room
				val chatEntity = ChatInfoEntity(
					id = chat.id,
					companyId = companyId,
					title = title,
					status = chat.status.name,
					createdAt = chat.createdAt,
					lastMessageTime = chat.lastMessageTime,
					lastMessageText = chat.lastMessageText,
					creatorId = chat.creatorId,
					assignedToId = chat.assignedToId,
					subject = chat.subject,
					unreadCount = 0 // New requests are unread
				)
				chatInfoDao.insertChat(chatEntity)
				
				// Save participants
				for (participant in chat.participants) {
					val participantEntity = ChatParticipantEntity(
						chatId = chat.id,
						userId = participant.userId,
						displayName = participant.displayName,
						role = participant.role.name,
						unreadCount = participant.unreadCount,
						lastReadTimestamp = participant.lastReadTimestamp
					)
					participantDao.insertParticipant(participantEntity)
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error fetching pending chats", e)
			throw e
		}
	}
	
	/**
	 * Fetch all chats assigned to the current SuperUser from Firebase and store in Room
	 */
	override suspend fun fetchAssignedChats() {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Fetching assigned chats for SuperUser: $userId in company: $companyId")
		
		try {
			// Query assigned chats
			val chatSnapshots = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.whereEqualTo("assignedToId", userId)
				.get()
				.await()
			
			// Process each chat
			for (chatDoc in chatSnapshots.documents) {
				val chat = chatDoc.toObject(Chat::class.java) ?: continue
				
				// Generate title for the SuperUser view
				val title = "Chat with ${getCreatorName(chat)}"
				
				// Save chat to Room
				val chatEntity = ChatInfoEntity(
					id = chat.id,
					companyId = companyId,
					title = title,
					status = chat.status.name,
					createdAt = chat.createdAt,
					lastMessageTime = chat.lastMessageTime,
					lastMessageText = chat.lastMessageText,
					creatorId = chat.creatorId,
					assignedToId = chat.assignedToId,
					subject = chat.subject,
					unreadCount = getUnreadCountForUser(chat, userId)
				)
				chatInfoDao.insertChat(chatEntity)
				
				// Save participants
				for (participant in chat.participants) {
					val participantEntity = ChatParticipantEntity(
						chatId = chat.id,
						userId = participant.userId,
						displayName = participant.displayName,
						role = participant.role.name,
						unreadCount = participant.unreadCount,
						lastReadTimestamp = participant.lastReadTimestamp
					)
					participantDao.insertParticipant(participantEntity)
				}
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error fetching assigned chats", e)
			throw e
		}
	}
	
	/**
	 * Create a new chat in Firebase
	 */
	override suspend fun createChatInFirebase(chat: Chat): String {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		val userName = getCurrentUserName()
		
		Log.d(TAG, "Creating new chat in company: $companyId by user: $userId")
		
		try {
			// Set up participant information
			val participants = listOf(
				Participant(
					userId = userId,
					displayName = userName,
					role = UserRole.USER,
					unreadCount = 0,
					lastReadTimestamp = System.currentTimeMillis()
				)
			)
			
			// Create chat with appropriate fields
			val newChat = chat.copy(
				participants = participants,
				creatorId = userId
			)
			
			// Save to Firebase
			val chatRef = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(newChat.id)
			
			// Create a map with participant userIds for easier querying
			val participantUserIds = participants.map { it.userId }
			val chatData = mapOf(
				"id" to newChat.id,
				"status" to newChat.status.name,
				"createdAt" to newChat.createdAt,
				"lastMessageTime" to newChat.lastMessageTime,
				"lastMessageText" to newChat.lastMessageText,
				"creatorId" to newChat.creatorId,
				"assignedToId" to newChat.assignedToId,
				"subject" to newChat.subject,
				"participants" to newChat.participants,
				"participantUserIds" to participantUserIds
			)
			
			chatRef.set(chatData).await()
			
			return newChat.id
		} catch (e: Exception) {
			Log.e(TAG, "Error creating chat in Firebase", e)
			throw e
		}
	}
	
	/**
	 * Save a chat to the local Room database
	 */
	override suspend fun saveChat(chat: Chat) {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Saving chat to Room: ${chat.id}")
		
		try {
			// Generate title based on current user role
			val title = generateChatTitle(chat)
			
			// Create and save chat entity
			val chatEntity = ChatInfoEntity(
				id = chat.id,
				companyId = companyId,
				title = title,
				status = chat.status.name,
				createdAt = chat.createdAt,
				lastMessageTime = chat.lastMessageTime,
				lastMessageText = chat.lastMessageText,
				creatorId = chat.creatorId,
				assignedToId = chat.assignedToId,
				subject = chat.subject,
				unreadCount = getUnreadCountForUser(chat, userId)
			)
			chatInfoDao.insertChat(chatEntity)
			
			// Save participants
			for (participant in chat.participants) {
				val participantEntity = ChatParticipantEntity(
					chatId = chat.id,
					userId = participant.userId,
					displayName = participant.displayName,
					role = participant.role.name,
					unreadCount = participant.unreadCount,
					lastReadTimestamp = participant.lastReadTimestamp
				)
				participantDao.insertParticipant(participantEntity)
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error saving chat to Room", e)
			throw e
		}
	}
	
	/**
	 * Update the status of a chat in Firebase
	 */
	override suspend fun updateChatStatus(chatId: String, status: ChatStatus) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Updating chat status in Firebase: $chatId to $status")
		
		try {
			// Update in Firebase
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.update("status", status.name)
				.await()
			
			// Update in Room
			chatInfoDao.updateChatStatus(chatId, status.name)
		} catch (e: Exception) {
			Log.e(TAG, "Error updating chat status", e)
			throw e
		}
	}
	
	/**
	 * Update the status of a chat for a specific user
	 */
	override suspend fun updateChatStatus(chatId: String, status: ChatStatus, userId: String) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Updating chat status for user: $chatId to $status for user $userId")
		
		try {
			// This is a special case for deletion, which is per-user
			// We'll add a field to mark it as deleted for this user
			val updateField = "deletedForUsers.${userId}"
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.update(updateField, true)
				.await()
			
			// If the current user is the one deleting, update Room too
			if (userId == getCurrentUserId()) {
				chatInfoDao.updateChatStatus(chatId, status.name)
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error updating chat status for user", e)
			throw e
		}
	}
	
	/**
	 * Assign a chat to a SuperUser
	 */
	override suspend fun assignChatToSuperUser(chatId: String, superUserId: String) {
		val companyId = userSession.getCurrentCompanyId()
		val superUserName = getSuperUserName(superUserId)
		
		Log.d(TAG, "Assigning chat: $chatId to SuperUser: $superUserId")
		
		try {
			// Get current chat to update participants
			val chatDoc = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.get()
				.await()
			
			val chat = chatDoc.toObject(Chat::class.java) ?: throw Exception("Chat not found")
			
			// Add SuperUser to participants if not already there
			val participantIds = chat.participants.map { it.userId }
			val updatedParticipants = chat.participants.toMutableList()
			
			if (superUserId !in participantIds) {
				updatedParticipants.add(
					Participant(
						userId = superUserId,
						displayName = superUserName,
						role = UserRole.SUPER_USER,
						unreadCount = 0,
						lastReadTimestamp = System.currentTimeMillis()
					)
				)
			}
			
			// Update participant userIds list
			val updatedParticipantIds = updatedParticipants.map { it.userId }
			
			// Update in Firebase
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.update(
					mapOf(
						"assignedToId" to superUserId,
						"participants" to updatedParticipants,
						"participantUserIds" to updatedParticipantIds
					)
				)
				.await()
			
			// Update in Room
			chatInfoDao.updateAssignedTo(chatId, superUserId)
			
			// Add SuperUser as participant in Room
			if (superUserId !in participantIds) {
				val participantEntity = ChatParticipantEntity(
					chatId = chatId,
					userId = superUserId,
					displayName = superUserName,
					role = UserRole.SUPER_USER.name,
					unreadCount = 0,
					lastReadTimestamp = System.currentTimeMillis()
				)
				participantDao.insertParticipant(participantEntity)
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error assigning chat to SuperUser", e)
			throw e
		}
	}
	
	/**
	 * Update the last message text for a chat
	 */
	override suspend fun updateLastMessage(chatId: String, lastMessage: String) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Updating last message for chat: $chatId")
		
		try {
			val timestamp = System.currentTimeMillis()
			
			// Update in Firebase
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.update(
					mapOf(
						"lastMessageText" to lastMessage,
						"lastMessageTime" to timestamp
					)
				)
				.await()
			
			// Update in Room
			chatInfoDao.updateLastMessage(chatId, lastMessage, timestamp)
		} catch (e: Exception) {
			Log.e(TAG, "Error updating last message", e)
			throw e
		}
	}
	
	/**
	 * Delete a chat request from Firebase
	 */
	override suspend fun deleteChatRequest(chatId: String) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Deleting chat request: $chatId")
		
		try {
			// Delete from Firebase
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.delete()
				.await()
			
			// Delete from Room
			deleteChatLocally(chatId)
		} catch (e: Exception) {
			Log.e(TAG, "Error deleting chat request", e)
			throw e
		}
	}
	
	/**
	 * Delete a chat from the local Room database only
	 */
	override suspend fun deleteChatLocally(chatId: String) {
		Log.d(TAG, "Deleting chat locally: $chatId")
		
		try {
			// Delete chat info
			chatInfoDao.deleteChat(chatId)
			
			// Delete participants
			participantDao.deleteParticipantsForChat(chatId)
			
			// Delete messages
			messageDao.deleteMessagesForChat(chatId)
		} catch (e: Exception) {
			Log.e(TAG, "Error deleting chat locally", e)
			throw e
		}
	}
	
	/**
	 * Get messages for a specific chat from Room
	 */
	override fun getMessages(chatId: String): Flow<List<Message>> {
		Log.d(TAG, "Getting messages for chat: $chatId")
		
		return messageDao.getMessagesForChat(chatId).map { entities ->
			entities.map { it.toMessage() }
		}
	}
	
	/**
	 * Get the total number of messages in a chat
	 */
	override suspend fun getMessageCount(chatId: String): Int {
		return messageDao.getMessageCount(chatId)
	}
	
	/**
	 * Fetch the most recent messages for a chat from Firebase and store in Room
	 */
	override suspend fun fetchRecentMessages(chatId: String, count: Int) {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		Log.d(TAG, "Fetching $count recent messages for chat: $chatId")
		
		try {
			// Query most recent messages
			val messageSnapshots = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.collection("messages")
				.orderBy("timestamp", Query.Direction.DESCENDING)
				.limit(count.toLong())
				.get()
				.await()
			
			val messagesToMarkAsReceived = mutableListOf<String>()
			
			// Process each message
			for (messageDoc in messageSnapshots.documents) {
				val message = messageDoc.toObject(Message::class.java) ?: continue
				
				// Check if message is for current user and mark as received
				if (message.senderId != userId && message.status != MessageStatus.READ) {
					messagesToMarkAsReceived.add(message.id)
				}
				
				// Save to Room
				val messageEntity = MessageEntity(
					id = message.id,
					chatId = message.chatId,
					senderId = message.senderId,
					senderName = message.senderName,
					content = message.content,
					timestamp = message.timestamp,
					status = message.status.name,
					isSystemMessage = message.isSystemMessage
				)
				messageDao.insertMessage(messageEntity)
			}
			
			// Mark messages as received
			if (messagesToMarkAsReceived.isNotEmpty()) {
				updateMessagesStatus(messagesToMarkAsReceived, MessageStatus.DELIVERED)
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error fetching recent messages", e)
			throw e
		}
	}
	
	/**
	 * Fetch older messages for pagination
	 */
	override suspend fun fetchOlderMessages(chatId: String, skip: Int, count: Int): Int {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Fetching older messages for chat: $chatId, skip: $skip, count: $count")
		
		try {
			// Get the oldest message we currently have
			val oldestMessage = messageDao.getOldestMessage(chatId)
			
			// If we don't have any messages, fetch recent ones instead
			if (oldestMessage == null) {
				fetchRecentMessages(chatId, count)
				return messageDao.getMessageCount(chatId)
			}
			
			// Query messages older than our oldest one
			val messageSnapshots = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.collection("messages")
				.orderBy("timestamp", Query.Direction.DESCENDING)
				.whereLessThan("timestamp", oldestMessage.timestamp)
				.limit(count.toLong())
				.get()
				.await()
			
			// Process each message
			for (messageDoc in messageSnapshots.documents) {
				val message = messageDoc.toObject(Message::class.java) ?: continue
				
				// Save to Room
				val messageEntity = MessageEntity(
					id = message.id,
					chatId = message.chatId,
					senderId = message.senderId,
					senderName = message.senderName,
					content = message.content,
					timestamp = message.timestamp,
					status = message.status.name,
					isSystemMessage = message.isSystemMessage
				)
				messageDao.insertMessage(messageEntity)
			}
			
			// Return updated count
			return messageDao.getMessageCount(chatId)
		} catch (e: Exception) {
			Log.e(TAG, "Error fetching older messages", e)
			throw e
		}
	}
	
	/**
	 * Save a message to the local Room database
	 */
	override suspend fun saveMessage(message: Message) {
		Log.d(TAG, "Saving message to Room: ${message.id}")
		
		try {
			val messageEntity = MessageEntity(
				id = message.id,
				chatId = message.chatId,
				senderId = message.senderId,
				senderName = message.senderName,
				content = message.content,
				timestamp = message.timestamp,
				status = message.status.name,
				isSystemMessage = message.isSystemMessage
			)
			messageDao.insertMessage(messageEntity)
		} catch (e: Exception) {
			Log.e(TAG, "Error saving message to Room", e)
			throw e
		}
	}
	
	/**
	 * Send a message to Firebase
	 */
	override suspend fun sendMessageToFirebase(message: Message) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Sending message to Firebase: ${message.id}")
		
		try {
			// Add message to Firebase
			val messageRef = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(message.chatId)
				.collection("messages")
				.document(message.id)
			
			messageRef.set(message).await()
			
			// Update last message in chat
			updateLastMessage(message.chatId, message.content)
			
			// Increment unread count for all other participants
			if (!message.isSystemMessage) {
				incrementUnreadCountForOthers(message.chatId, message.senderId)
			}
		} catch (e: Exception) {
			Log.e(TAG, "Error sending message to Firebase", e)
			throw e
		}
	}
	
	/**
	 * Update the status of a message
	 */
	override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Updating message status: $messageId to $status")
		
		try {
			// Get message to find its chat
			val messageEntity = messageDao.getMessageById(messageId) ?: return
			
			// Update in Firebase
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(messageEntity.chatId)
				.collection("messages")
				.document(messageId)
				.update("status", status.name)
				.await()
			
			// Update in Room
			messageDao.updateMessageStatus(messageId, status.name)
		} catch (e: Exception) {
			Log.e(TAG, "Error updating message status", e)
			// Don't throw - this is a non-critical operation
		}
	}
	
	/**
	 * Update the status of multiple messages
	 */
	override suspend fun updateMessagesStatus(messageIds: List<String>, status: MessageStatus) {
		for (messageId in messageIds) {
			updateMessageStatus(messageId, status)
		}
	}
	
	/**
	 * Mark all messages in a chat as read for a user
	 */
	override suspend fun markChatAsRead(chatId: String, userId: String) {
		Log.d(TAG, "Marking chat as read: $chatId for user: $userId")
		
		try {
			// Update messages in Room
			messageDao.markMessagesAsRead(chatId, userId)
			
			// Reset unread count in Room
			chatInfoDao.resetUnreadCount(chatId)
			participantDao.resetUnreadCount(chatId, userId)
		} catch (e: Exception) {
			Log.e(TAG, "Error marking chat as read", e)
			throw e
		}
	}
	
	/**
	 * Update the last read timestamp in Firebase
	 */
	override suspend fun updateReadTimestampInFirebase(chatId: String, userId: String) {
		val companyId = userSession.getCurrentCompanyId()
		
		Log.d(TAG, "Updating read timestamp in Firebase for chat: $chatId, user: $userId")
		
		try {
			// Find participant index
			val chatDoc = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.get()
				.await()
			
			val chat = chatDoc.toObject(Chat::class.java) ?: return
			val timestamp = System.currentTimeMillis()
			
			// Find participant index
			val participantIndex = chat.participants.indexOfFirst { it.userId == userId }
			if (participantIndex == -1) return
			
			// Update last read timestamp and reset unread count
			firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.update(
					mapOf(
						"participants.$participantIndex.lastReadTimestamp" to timestamp,
						"participants.$participantIndex.unreadCount" to 0
					)
				)
				.await()
		} catch (e: Exception) {
			Log.e(TAG, "Error updating read timestamp in Firebase", e)
			// Don't throw - this is a non-critical operation
		}
	}
	
	/**
	 * Get the ID of the current user
	 */
	override fun getCurrentUserId(): String {
		return userSession.getCurrentUserId()
	}
	
	/**
	 * Get the display name of the current user
	 */
	override fun getCurrentUserName(): String {
		return userSession.getCurrentUserName()
	}
	
	/**
	 * Check if the current user is a SuperUser
	 */
	override fun isCurrentUserSuperUser(): Boolean {
		return userSession.getCurrentUserRole() == UserRole.SUPER_USER ||
				userSession.getCurrentUserRole() == UserRole.ADMIN
	}
	
	/**
	 * Get the total number of unread messages across all chats
	 */
	override fun getTotalUnreadCount(): Flow<Int> {
		val companyId = userSession.getCurrentCompanyId()
		val userId = getCurrentUserId()
		
		return chatInfoDao.getTotalUnreadCount(companyId, userId)
	}
	
	// Private helper methods
	
	/**
	 * Generate an appropriate chat title based on user role
	 */
	private fun generateChatTitle(chat: Chat): String {
		val userId = getCurrentUserId()
		val isSuperUser = isCurrentUserSuperUser()
		
		return when {
			// For pending chats
			chat.status == ChatStatus.PENDING -> "Request: ${chat.subject}"
			
			// For SuperUsers, show the requester's name
			isSuperUser && chat.creatorId != userId -> {
				val creatorName = getParticipantName(chat, chat.creatorId)
				"Chat with $creatorName"
			}
			
			// For regular users, show the SuperUser's name
			chat.assignedToId != null && chat.assignedToId != userId -> {
				val superUserName = getParticipantName(chat, chat.assignedToId)
				"Chat with $superUserName"
			}
			
			// Fallback to subject
			else -> chat.subject
		}
	}
	
	/**
	 * Get a participant's name from the chat
	 */
	private fun getParticipantName(chat: Chat, userId: String): String {
		return chat.participants.find { it.userId == userId }?.displayName ?: "Unknown User"
	}
	
	/**
	 * Get the name of the chat creator
	 */
	private fun getCreatorName(chat: Chat): String {
		return getParticipantName(chat, chat.creatorId)
	}
	
	/**
	 * Get unread count for a specific user
	 */
	private fun getUnreadCountForUser(chat: Chat, userId: String): Int {
		return chat.participants.find { it.userId == userId }?.unreadCount ?: 0
	}
	
	/**
	 * Get a SuperUser's name by ID
	 */
	private suspend fun getSuperUserName(superUserId: String): String {
		val companyId = userSession.getCurrentCompanyId()
		
		try {
			val userDoc = firestore
				.collection("companies")
				.document(companyId)
				.collection("users")
				.document(superUserId)
				.get()
				.await()
			
			return userDoc.getString("displayName") ?: "BaseBuddy"
		} catch (e: Exception) {
			Log.e(TAG, "Error getting SuperUser name", e)
			return "BaseBuddy"
		}
	}
	
	/**
	 * Increment unread count for all participants except sender
	 */
	private suspend fun incrementUnreadCountForOthers(chatId: String, senderId: String) {
		val companyId = userSession.getCurrentCompanyId()
		
		try {
			// Get current chat
			val chatDoc = firestore
				.collection("messaging")
				.document(companyId)
				.collection("chats")
				.document(chatId)
				.get()
				.await()
			
			val chat = chatDoc.toObject(Chat::class.java) ?: return
			
			// Update each participant except sender
			chat.participants.forEachIndexed { index, participant ->
				if (participant.userId != senderId) {
					// Increment unread count in Firebase
					firestore
						.collection("messaging")
						.document(companyId)
						.collection("chats")
						.document(chatId)
						.update(
							"participants.$index.unreadCount",
							participant.unreadCount + 1
						).await()
					
					// Update in Room
					participantDao.incrementUnreadCount(chatId, participant.userId)
				}
			}
			
			// Update total unread in chat info
			chatInfoDao.incrementUnreadCount(chatId)
		} catch (e: Exception) {
			Log.e(TAG, "Error incrementing unread count", e)
			// Don't throw - this is a non-critical operation
		}
	}
	
	// Extension functions to convert between entity and model classes
	
	private fun ChatInfoEntity.toChatInfo(): ChatInfo {
		return ChatInfo(
			id = id,
			title = title,
			status = ChatStatus.valueOf(status),
			createdAt = createdAt,
			lastMessageTime = lastMessageTime,
			lastMessageText = lastMessageText,
			creatorId = creatorId,
			assignedToId = assignedToId,
			unreadCount = unreadCount,
			isAssignedToCurrentUser = assignedToId == getCurrentUserId(),
			subject = subject
		)
	}
	
	private fun MessageEntity.toMessage(): Message {
		return Message(
			id = id,
			chatId = chatId,
			senderId = senderId,
			senderName = senderName,
			content = content,
			timestamp = timestamp,
			status = MessageStatus.valueOf(status),
			isSystemMessage = isSystemMessage
		)
	}
}