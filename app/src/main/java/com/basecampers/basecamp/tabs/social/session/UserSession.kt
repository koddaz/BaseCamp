/*
package com.basecampers.basecamp.tabs.social.session

import com.basecampers.basecamp.tabs.profile.models.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

*/
/**
 * Manages current user information and session state.
 *//*

@Singleton
class UserSession @Inject constructor(
	private val auth: FirebaseAuth,
	private val firestore: FirebaseFirestore
) {
	private var currentCompanyId: String = ""
	private var currentUserStatus: UserStatus = UserStatus.USER
	private var currentUserName: String = ""
	
	*/
/**
	 * Initialize user session with necessary data.
	 * Call this when user selects a company.
	 *//*

	suspend fun initializeForCompany(companyId: String) {
		this.currentCompanyId = companyId
		refreshUserInfo()
	}
	
	*/
/**
	 * Refresh current user information from Firestore.
	 *//*

	suspend fun refreshUserInfo() {
		val userId = getCurrentUserId()
		
		try {
			val userDoc = firestore
				.collection("companies")
				.document(currentCompanyId)
				.collection("users")
				.document(userId)
				.get()
				.await()
			
			// Extract role
			val roleString = userDoc.getString("role") ?: UserStatus.USER.name
			currentUserStatus = try {
				UserStatus.valueOf(roleString)
			} catch (e: Exception) {
				UserStatus.USER
			}
			
			// Extract name
			val firstName = userDoc.getString("firstName") ?: ""
			val lastName = userDoc.getString("lastName") ?: ""
			currentUserName = if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
				"$firstName $lastName"
			} else {
				auth.currentUser?.displayName ?: "User"
			}
		} catch (e: Exception) {
			// Default to USER role if there's an error
			currentUserStatus = UserStatus.USER
			currentUserName = auth.currentUser?.displayName ?: "User"
		}
	}
	
	*/
/**
	 * Get the current company ID.
	 *//*

	fun getCurrentCompanyId(): String = currentCompanyId
	
	*/
/**
	 * Get the current user ID.
	 *//*

	fun getCurrentUserId(): String = auth.currentUser?.uid ?: ""
	
	*/
/**
	 * Get the current user's role.
	 *//*

	fun getCurrentUserStatus(): UserStatus = currentUserStatus
	
	*/
/**
	 * Get the current user's display name.
	 *//*

	fun getCurrentUserName(): String = currentUserName
	
	*/
/**
	 * Check if the current user is logged in.
	 *//*

	fun isLoggedIn(): Boolean = auth.currentUser != null
}*/
