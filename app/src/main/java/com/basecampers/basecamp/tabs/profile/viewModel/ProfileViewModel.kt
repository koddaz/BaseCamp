package com.basecampers.basecamp.tabs.profile.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing user profile data
 */
class ProfileViewModel : ViewModel() {
	private val TAG = "ProfileViewModel"
	private val firestore = Firebase.firestore
	
	// Profile data
	private val _profile = MutableStateFlow<ProfileModel?>(null)
	val profile = _profile.asStateFlow()
	
	// Loading state
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	private val _companyNames = MutableStateFlow<Map<String, String>>(emptyMap())
	val companyNames = _companyNames.asStateFlow()

	init {
		// Initialize by observing UserSession profile
		viewModelScope.launch {
			UserSession.profile.collect { profile ->
				_profile.value = profile
			}

		}
		
		// Load profile data if user is logged in
		getCurrentUserId()?.let { fetchProfile(it) }
	}
	
	/**
	 * Fetches user profile from Firestore
	 */
	fun fetchCompanyNames() {
		val companyIds = _profile.value?.companyList ?: return
		if (companyIds.isEmpty()) return

		companyIds.forEach { companyId ->
			firestore.collection("companies").document(companyId).get()
				.addOnSuccessListener { document ->
					if (document != null && document.exists()) {
						val companyName = document.getString("companyName") ?: companyId

						// Update ViewModel state
						val currentMap = _companyNames.value.toMutableMap()
						currentMap[companyId] = companyName
						_companyNames.value = currentMap

						// Update UserSession
						UserSession.updateCompanyName(companyId, companyName)
					}
				}
		}
	}
	fun fetchProfile(userId: String) {
		_isLoading.value = true
		
		firestore.collection("users").document(userId).get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val profileModel = document.toObject(ProfileModel::class.java)
					profileModel?.let {
						// Update ViewModel state
						_profile.value = it
						
						// Update UserSession
						UserSession.setProfile(it)
						
						Log.d(TAG, "Successfully fetched profile for $userId")
					} ?: run {
						// Create a basic profile from document fields if mapping failed
						val basicProfile = ProfileModel(
							id = userId,
							email = document.getString("email") ?: "",
							firstName = document.getString("firstName") ?: "",
							lastName = document.getString("lastName") ?: "",
							companyList = document.get("companyList") as? List<String> ?: emptyList()
						)
						_profile.value = basicProfile
						UserSession.setProfile(basicProfile)
					}
				} else {
					_profile.value = null
					Log.d(TAG, "No profile document exists for $userId")
				}
				_isLoading.value = false
			}
			.addOnFailureListener { e ->
				_profile.value = null
				_isLoading.value = false
				Log.e(TAG, "Failed to fetch profile", e)
			}
	}
	
	/**
	 * Creates a new user profile
	 */
	fun createProfile(email: String, userId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
		// Basic user data
		val profileModel = ProfileModel(
			id = userId,
			email = email,
			firstName = "",
			lastName = "",
			companyList = emptyList()
		)
		
		// Set to local state
		_profile.value = profileModel
		
		// Update UserSession
		UserSession.setProfile(profileModel)
		
		// Create full profile document
		firestore.collection("users").document(userId)
			.set(profileModel)
			.addOnSuccessListener {
				Log.d(TAG, "Profile created successfully")
				onSuccess()
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Failed to create profile in Firestore", e)
				onError("Failed to create user profile: ${e.message}")
			}
	}
	
	/**
	 * Updates profile information
	 */
	fun updateProfile(
		firstName: String,
		lastName: String,
		onSuccess: () -> Unit = {},
		onError: (String) -> Unit = {}
	) {
		val userId = getCurrentUserId() ?: run {
			onError("Not logged in")
			return
		}
		
		_isLoading.value = true
		
		// Create updated profile
		val currentProfile = _profile.value
		val updatedProfile = currentProfile?.copy(
			firstName = firstName,
			lastName = lastName
		) ?: run {
			onError("No existing profile found")
			_isLoading.value = false
			return
		}
		
		// Update Firestore
		firestore.collection("users").document(userId)
			.update(
				mapOf(
					"firstName" to firstName,
					"lastName" to lastName
				)
			)
			.addOnSuccessListener {
				// Update local state
				_profile.value = updatedProfile
				
				// Update UserSession
				UserSession.setProfile(updatedProfile)
				
				_isLoading.value = false
				onSuccess()
				Log.d(TAG, "Successfully updated profile for $userId")
			}
			.addOnFailureListener { e ->
				_isLoading.value = false
				onError("Failed to update profile: ${e.message}")
				Log.e(TAG, "Failed to update profile", e)
			}
	}
	
	/**
	 * Gets current user ID from Firebase Auth
	 */
	fun getCurrentUserId(): String? {
		return Firebase.auth.currentUser?.uid
	}
	
	/**
	 * Updates user's name in Firestore and UserSession
	 *
	 */
	fun updateUserName(
		firstName: String,
		lastName: String,
		onSuccess: () -> Unit = {},
		onError: (String) -> Unit = {}
	) {
		val userId = getCurrentUserId() ?: run {
			onError("User not logged in")
			return
		}
		
		// Create a map of fields to update
		val updates = mapOf(
			"firstName" to firstName,
			"lastName" to lastName
		)
		
		// Update Firestore first
		firestore.collection("users").document(userId)
			.update(updates)
			.addOnSuccessListener {
				// After Firestore success, update UserSession
				val currentProfile = UserSession.profile.value
				if (currentProfile != null) {
					// Create updated profile
					val updatedProfile = currentProfile.copy(
						firstName = firstName,
						lastName = lastName
					)
					
					// Update UserSession
					UserSession.setProfile(updatedProfile)
					
					Log.d("ProfileViewModel", "Name updated successfully")
					onSuccess()
				} else {
					onError("Profile not found in session")
				}
			}
			.addOnFailureListener { e ->
				Log.e("ProfileViewModel", "Failed to update name", e)
				onError("Failed to update name: ${e.message}")
			}
	}
}