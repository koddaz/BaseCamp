package com.example.basecamp.tabs.social.messaging.db.dao

import androidx.room.*
import com.example.basecamp.tabs.social.messaging.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for messages.
 */
@Dao
interface MessageDao {
	/**
	 * Get all messages for a chat.
	 */
	@Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC")
	fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>
	
	/**
	 * Get the number of messages in a chat.
	 */
	@Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId")
	suspend fun getMessageCount(chatId: String): Int
	
	/**
	 * Get a message by its ID.
	 */
	@Query("SELECT * FROM messages WHERE id = :messageId")
	suspend fun getMessageById(messageId: String): MessageEntity?
	
	/**
	 * Get the oldest message in a chat.
	 */
	@Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC LIMIT 1")
	suspend fun getOldestMessage(chatId: String): MessageEntity?
	
	/**
	 * Insert or replace a message.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertMessage(message: MessageEntity)
	
	/**
	 * Delete all messages of a chat.
	 */
	@Query("DELETE FROM messages WHERE chatId = :chatId")
	suspend fun deleteMessagesForChat(chatId: String)
	
	/**
	 * Update the status of a message.
	 */
	@Query("UPDATE messages SET status = :status WHERE id = :messageId")
	suspend fun updateMessageStatus(messageId: String, status: String)
	
	/**
	 * Mark all messages in a chat as read for a specific user.
	 */
	@Query("UPDATE messages SET status = 'READ' WHERE chatId = :chatId AND senderId != :userId AND status != 'READ'")
	suspend fun markMessagesAsRead(chatId: String, userId: String)
}