package com.example.basecamp.tabs.social.messaging.db.dao

import androidx.room.*
import com.example.basecamp.tabs.social.messaging.db.entity.ChatInfoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for chat information.
 */
@Dao
interface ChatInfoDao {
	/**
	 * Get a chat by its ID.
	 */
	@Query("SELECT * FROM chat_info WHERE id = :chatId")
	fun getChatById(chatId: String): Flow<ChatInfoEntity?>
	
	/**
	 * Get chats with a specific status from a specific company.
	 */
	@Query(
		"SELECT ci.* FROM chat_info ci JOIN chat_participants cp ON ci.id = cp.chatId " +
				"WHERE ci.status = :status AND ci.companyId = :companyId AND cp.userId = :userId"
	)
	fun getChatsByStatusAndParticipant(status: String, userId: String, companyId: String): Flow<List<ChatInfoEntity>>
	
	/**
	 * Get pending chats for a company.
	 */
	@Query(
		"SELECT * FROM chat_info WHERE status = 'PENDING' AND assignedToId IS NULL AND companyId = :companyId"
	)
	fun getPendingChats(companyId: String): Flow<List<ChatInfoEntity>>
	
	/**
	 * Get chats assigned to a specific SuperUser.
	 */
	@Query(
		"SELECT * FROM chat_info WHERE assignedToId = :userId AND status = 'ACTIVE' AND companyId = :companyId"
	)
	fun getAssignedChats(userId: String, companyId: String): Flow<List<ChatInfoEntity>>
	
	/**
	 * Get closed chats that were assigned to a specific SuperUser.
	 */
	@Query(
		"SELECT * FROM chat_info WHERE assignedToId = :userId AND status = 'CLOSED' AND companyId = :companyId"
	)
	fun getClosedAssignedChats(userId: String, companyId: String): Flow<List<ChatInfoEntity>>
	
	/**
	 * Insert or replace a chat.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertChat(chat: ChatInfoEntity)
	
	/**
	 * Delete a chat.
	 */
	@Query("DELETE FROM chat_info WHERE id = :chatId")
	suspend fun deleteChat(chatId: String)
	
	/**
	 * Update the status of a chat.
	 */
	@Query("UPDATE chat_info SET status = :status WHERE id = :chatId")
	suspend fun updateChatStatus(chatId: String, status: String)
	
	/**
	 * Update the assigned SuperUser of a chat.
	 */
	@Query("UPDATE chat_info SET assignedToId = :superUserId WHERE id = :chatId")
	suspend fun updateAssignedTo(chatId: String, superUserId: String)
	
	/**
	 * Update the last message of a chat.
	 */
	@Query("UPDATE chat_info SET lastMessageText = :lastMessage, lastMessageTime = :timestamp WHERE id = :chatId")
	suspend fun updateLastMessage(chatId: String, lastMessage: String, timestamp: Long)
	
	/**
	 * Reset the unread count of a chat.
	 */
	@Query("UPDATE chat_info SET unreadCount = 0 WHERE id = :chatId")
	suspend fun resetUnreadCount(chatId: String)
	
	/**
	 * Increment the unread count of a chat.
	 */
	@Query("UPDATE chat_info SET unreadCount = unreadCount + 1 WHERE id = :chatId")
	suspend fun incrementUnreadCount(chatId: String)
	
	/**
	 * Get the total unread count across all chats for a user.
	 */
	@Query(
		"SELECT SUM(unreadCount) FROM chat_info WHERE companyId = :companyId " +
				"AND id IN (SELECT chatId FROM chat_participants WHERE userId = :userId)"
	)
	fun getTotalUnreadCount(companyId: String, userId: String): Flow<Int>
}