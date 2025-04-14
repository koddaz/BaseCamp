package com.example.basecamp.tabs.social.messaging.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.basecamp.tabs.social.messaging.db.dao.ChatInfoDao
import com.example.basecamp.tabs.social.messaging.db.dao.ChatParticipantDao
import com.example.basecamp.tabs.social.messaging.db.dao.MessageDao
import com.example.basecamp.tabs.social.messaging.db.entity.ChatInfoEntity
import com.example.basecamp.tabs.social.messaging.db.entity.ChatParticipantEntity
import com.example.basecamp.tabs.social.messaging.db.entity.MessageEntity

/**
 * Room database for messaging functionality.
 */
@Database(
	entities = [
		ChatInfoEntity::class,
		ChatParticipantEntity::class,
		MessageEntity::class
	],
	version = 1,
	exportSchema = false
)
abstract class MessagingDatabase : RoomDatabase() {
	/**
	 * Get the DAO for chat information.
	 */
	abstract fun chatInfoDao(): ChatInfoDao
	
	/**
	 * Get the DAO for chat participants.
	 */
	abstract fun chatParticipantDao(): ChatParticipantDao
	
	/**
	 * Get the DAO for messages.
	 */
	abstract fun messageDao(): MessageDao
	
	companion object {
		@Volatile
		private var INSTANCE: MessagingDatabase? = null
		
		/**
		 * Get the singleton instance of the database.
		 */
		fun getInstance(context: Context): MessagingDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					MessagingDatabase::class.java,
					"messaging_database"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}