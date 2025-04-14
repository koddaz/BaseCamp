package com.example.basecamp.tabs.social.messaging.db.dao

import androidx.room.*
import com.example.basecamp.tabs.social.messaging.db.entity.ChatParticipantEntity

/**
 * Data Access Object for chat participants.
 */
@Dao
interface ChatParticipantDao {
	/**
	 * Insert or replace a participant.
	 */
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertParticipant(participant: ChatParticipantEntity)
	
	/**
	 * Delete all participants of a chat.
	 */
	@Query("DELETE FROM chat_participants WHERE chatId = :chatId")
	suspend fun deleteParticipantsForChat(chatId: String)
	
	/**
	 * Reset the unread count of a participant.
	 */
	@Query("UPDATE chat_participants SET unreadCount = 0 WHERE chatId = :chatId AND userId = :userId")
	suspend fun resetUnreadCount(chatId: String, userId: String)
	
	/**
	 * Increment the unread count of a participant.
	 */
	@Query("UPDATE chat_participants SET unreadCount = unreadCount + 1 WHERE chatId = :chatId AND userId = :userId")
	suspend fun incrementUnreadCount(chatId: String, userId: String)
}