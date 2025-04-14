package com.example.basecamp.tabs.social.messaging

import android.content.Context
import com.example.basecamp.tabs.social.messaging.db.MessagingDatabase
import com.example.basecamp.tabs.social.messaging.repository.ChatRepository
import com.example.basecamp.tabs.social.messaging.repository.ChatRepositoryImpl
import com.example.basecamp.tabs.social.session.UserSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Service locator for messaging components.
 * A simpler alternative to dependency injection.
 */
object ServiceLocator {
	private var database: MessagingDatabase? = null
	private var chatRepository: ChatRepository? = null
	private var userSession: UserSession? = null
	
	/**
	 * Get the user session.
	 */
	fun getUserSession(context: Context): UserSession {
		return userSession ?: createUserSession().also {
			userSession = it
		}
	}
	
	/**
	 * Get the chat repository.
	 */
	fun getChatRepository(context: Context): ChatRepository {
		return chatRepository ?: createChatRepository(context).also {
			chatRepository = it
		}
	}
	
	/**
	 * Create the messaging database.
	 */
	private fun getDatabase(context: Context): MessagingDatabase {
		return database ?: MessagingDatabase.getInstance(context).also {
			database = it
		}
	}
	
	/**
	 * Create the user session.
	 */
	private fun createUserSession(): UserSession {
		val auth = FirebaseAuth.getInstance()
		val firestore = FirebaseFirestore.getInstance()
		return UserSession(auth, firestore)
	}
	
	/**
	 * Create the chat repository.
	 */
	private fun createChatRepository(context: Context): ChatRepository {
		val db = getDatabase(context)
		val firestore = FirebaseFirestore.getInstance()
		val userSession = getUserSession(context)
		
		return ChatRepositoryImpl(
			firestore,
			db.chatInfoDao(),
			db.chatParticipantDao(),
			db.messageDao(),
			userSession
		)
	}
}