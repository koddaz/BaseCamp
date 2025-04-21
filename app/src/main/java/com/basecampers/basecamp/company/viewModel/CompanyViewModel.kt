package com.basecampers.basecamp.company.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyModel
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URL
import java.util.UUID

/**
 * ViewModel handling all company-related operations.
 */
class CompanyViewModel : ViewModel() {
	private val TAG = "CompanyViewModel"
	
	// Company state flows
	private val _hasSelectedCompany = MutableStateFlow(false)
	val hasSelectedCompany: StateFlow<Boolean> = _hasSelectedCompany.asStateFlow()
	
	private val _currentCompanyId = MutableStateFlow<String?>(null)
	val currentCompanyId: StateFlow<String?> = _currentCompanyId.asStateFlow()
	
	private val _companies = MutableStateFlow<List<CompanyModel>>(emptyList())
	val companies: StateFlow<List<CompanyModel>> = _companies.asStateFlow()
	
	private val _companyProfile = MutableStateFlow<CompanyProfileModel?>(null)
	val companyProfile = _companyProfile.asStateFlow()
	
	// Add this to track company list changes
	private val _userCompanyList = MutableStateFlow<List<String>>(emptyList())
	val userCompanyList: StateFlow<List<String>> = _userCompanyList.asStateFlow()
	
	private val db = FirebaseFirestore.getInstance()
	
	init {
		fetchCompanies()
		
		// Initialize company list from UserSession
		val userId = UserSession.userId.value
		if (userId != null) {
			viewModelScope.launch {
				UserSession.profile.collect { profile ->
					if (profile != null) {
						_userCompanyList.value = profile.companyList
						Log.d(TAG, "Initial company list loaded: ${profile.companyList}")
					}
				}
			}
		}
	}
	
	//=== COMPANY LIST AND SEARCH FUNCTIONS ===//
	
	/**
	 * Fetches all companies from Firestore and updates local state.
	 */
	fun fetchCompanies() {
		db.collection("companies")
			.addSnapshotListener { snapshot, error ->
				if(error != null) {
					return@addSnapshotListener
				}
				val companyList = snapshot?.documents?.mapNotNull { doc ->
					try {
						CompanyModel(
							companyName = doc.getString("companyName") ?: "",
							ownerUID = doc.getString("ownerUID") ?: "",
							companyId = doc.id,
							bio = doc.getString("bio") ?: "No bio yet",
							imageUrl = doc.getString("imageUrl")?.let { URL(it) }
						)
					} catch (e: Exception) {
						null
					}
				} ?: emptyList()
				_companies.value = companyList
			}
	}
	
	/**
	 * Searches for user in all companies and updates UserSession with company list.
	 * This is crucial for synchronizing company memberships across the app.
	 */
	fun searchUserInCompanies(userId: String) {
		Log.d(TAG, "Searching for user $userId in companies")
		db.collection("companies")
			.get()
			.addOnSuccessListener { companySnap ->
				var userFound = false
				val foundCompanyIds = mutableListOf<String>()
				
				if (companySnap.isEmpty) {
					Log.e(TAG, "No companies found")
					return@addOnSuccessListener
				}
				
				for (companyDoc in companySnap.documents) {
					val companyId = companyDoc.id
					Log.d(TAG, "Checking company: $companyId")
					db.collection("companies")
						.document(companyId)
						.collection("users")
						.document(userId)
						.get()
						.addOnSuccessListener { userSnap ->
							if (userSnap.exists()) {
								userFound = true
								foundCompanyIds.add(companyId)
								Log.d(TAG, "Found user in company: $companyId")
								createUserCompanyProfile(userSnap, userId, companyId)
							}
						}
				}
				
				viewModelScope.launch {
					delay(1000) // Wait for async calls to complete
					if (!userFound) {
						Log.e(TAG, "User not found in any company")
					} else {
						Log.d(TAG, "Found user in ${foundCompanyIds.size} companies")
						
						// Update profile in UserSession with company list
						val currentProfile = UserSession.profile.value
						if (currentProfile != null && currentProfile.companyList != foundCompanyIds) {
							Log.d(TAG, "Updating user profile with companies: $foundCompanyIds")
							
							// Update profile in Firestore and UserSession
							val updatedProfile = currentProfile.copy(companyList = foundCompanyIds)
							db.collection("users").document(userId)
								.update("companyList", foundCompanyIds)
								.addOnSuccessListener {
									UserSession.setProfile(updatedProfile)
									_userCompanyList.value = foundCompanyIds
									Log.d(TAG, "Profile updated with companies")
								}
						}
					}
				}
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Failed to fetch companies", e)
			}
	}
	
	/**
	 * Creates a CompanyProfileModel from a Firestore document and updates UserSession.
	 */
	private fun createUserCompanyProfile(userDoc: DocumentSnapshot, userId: String, companyId: String?) {
		try {
			val imageUrlString = userDoc.getString("imageUrl")
			val imageUrl = if (!imageUrlString.isNullOrEmpty()) {
				try { URL(imageUrlString) } catch (e: Exception) { null }
			} else null
			val bio = userDoc.getString("bio") ?: "No bio yet"
			
			// Determine user status
			val status = when {
				userDoc.getBoolean("isAdmin") == true -> UserStatus.ADMIN
				userDoc.getString("status") == "SUPER_USER" -> UserStatus.SUPER_USER
				else -> UserStatus.USER
			}
			
			val companyProfileModel = CompanyProfileModel(
				imageUrl = imageUrl,
				bio = bio,
				status = status,
				id = userId,
				companyId = companyId ?: ""
			)
			
			_companyProfile.value = companyProfileModel
			UserSession.setCompanyProfile(companyProfileModel)
			Log.d(TAG, "Company profile created: $companyProfileModel")
		} catch (e: Exception) {
			Log.e(TAG, "Error creating CompanyProfileModel", e)
		}
	}
	
	//=== COMPANY SELECTION FUNCTIONS ===//
	
	/**
	 * Checks if a user has a selected company and loads it if available.
	 */
	suspend fun checkSelectedCompany(userId: String) {
		// Check if user has a selected company in local storage/preferences first
		val storedCompanyId = getStoredCompanyId()
		
		if (storedCompanyId != null) {
			_currentCompanyId.value = storedCompanyId
			_hasSelectedCompany.value = true
			
			// Update UserSession reference
			UserSession.setSelectedCompanyId(storedCompanyId)
			
			// Fetch company data for UserSession
			fetchCompanyData(storedCompanyId)
			fetchCompanyProfileData(userId, storedCompanyId)
			
			return
		}
		// If no stored company, check Firestore
		try {
			db.collection("users")
				.document(userId)
				.get()
				.addOnSuccessListener { document ->
					val lastCompanyId = document.getString("lastSelectedCompany")
					if (lastCompanyId != null) {
						_currentCompanyId.value = lastCompanyId
						_hasSelectedCompany.value = true
						storeCompanyId(lastCompanyId)
						
						// Update UserSession reference
						UserSession.setSelectedCompanyId(lastCompanyId)
						
						// Fetch company data for UserSession
						fetchCompanyData(lastCompanyId)
						fetchCompanyProfileData(userId, lastCompanyId)
					} else {
						_hasSelectedCompany.value = false
					}
				}
		} catch (e: Exception) {
			_hasSelectedCompany.value = false
		}
	}
	
	/**
	 * Selects a company for the current user and updates all related data.
	 */
	fun selectCompany(companyId: String, userId: String) {
		_currentCompanyId.value = companyId
		_hasSelectedCompany.value = true
		
		// Store in preferences
		storeCompanyId(companyId)
		
		// Update UserSession
		UserSession.setSelectedCompanyId(companyId)
		
		// Update in Firestore
		db.collection("users")
			.document(userId)
			.update("lastSelectedCompany", companyId)
		
		// Fetch company data
		fetchCompanyData(companyId)
		
		// Fetch user's role in this company
		fetchCompanyProfileData(userId, companyId)
		
		Log.d(TAG, "Selected company: $companyId")
	}
	
	/**
	 * Clears the currently selected company.
	 */
	fun clearSelectedCompany() {
		_currentCompanyId.value = null
		_hasSelectedCompany.value = false
		UserSession.setSelectedCompanyId(null)
		clearStoredCompanyId()
	}
	
	//=== COMPANY DATA FUNCTIONS ===//
	
	/**
	 * Fetches company data from Firestore and updates UserSession.
	 */
	fun fetchCompanyData(companyId: String) {
		db.collection("companies").document(companyId).get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val company = document.toObject(CompanyModel::class.java)
					company?.let {
						UserSession.setCompany(it)
					}
				}
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Failed to fetch company data", e)
			}
	}
	
	/**
	 * Fetches company profile data from Firestore and updates UserSession.
	 */
	fun fetchCompanyProfileData(userId: String, companyId: String) {
		db.collection("companies")
			.document(companyId)
			.collection("users")
			.document(userId)
			.get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val companyProfile = document.toObject(CompanyProfileModel::class.java)
					companyProfile?.let {
						// Update both ViewModel state and UserSession
						_companyProfile.value = it
						UserSession.setCompanyProfile(it)
					}
				}
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Failed to fetch company profile", e)
			}
	}
	
	//=== COMPANY MANAGEMENT FUNCTIONS ===//
	
	/**
	 * Creates a new company with the specified details.
	 */
	fun createCompany(
		companyName: String,
		userId: String,
		onSuccess: () -> Unit,
		onError: (String) -> Unit
	) {
		val companyId = UUID.randomUUID().toString()
		val companyInfo = CompanyModel(
			companyName = companyName,
			ownerUID = userId,
			companyId = companyId,
			bio = "No bio yet",
			imageUrl = null
		)
		val companyAdmin = CompanyProfileModel(
			imageUrl = null,
			bio = "No bio yet",
			status = UserStatus.ADMIN,
			id = userId,
			companyId = companyId
		)
		val companyRef = db.collection("companies").document(companyId)
		val userRef = db.collection("users").document(userId)
		val companyUserRef = companyRef.collection("users").document(userId)
		
		companyRef.set(companyInfo)
			.addOnSuccessListener {
				// Update UserSession
				UserSession.setCompany(companyInfo)
				
				userRef.update("companyList", FieldValue.arrayUnion(companyId))
					.addOnSuccessListener {
						companyUserRef.set(companyAdmin)
							.addOnSuccessListener {
								// Update UserSession
								UserSession.setCompanyProfile(companyAdmin)
								
								// Update company list in ViewModel
								val currentCompanyList = _userCompanyList.value
								if (!currentCompanyList.contains(companyId)) {
									_userCompanyList.value = currentCompanyList + companyId
								}
								
								selectCompany(companyId, userId)
								onSuccess()
							}
							.addOnFailureListener { e ->
								onError("Failed to create company admin: ${e.message}")
							}
					}
					.addOnFailureListener { e ->
						onError("Failed to update user profile: ${e.message}")
					}
			}
			.addOnFailureListener { e ->
				onError("Failed to create company: ${e.message}")
			}
	}
	
	/**
	 * Used during registration to create a new company and set the user as admin.
	 */
	fun createCompanyWithAdmin(
		userId: String,
		email: String,
		companyName: String,
		companyId: String,
		firstName: String,
		lastName: String,
		onSuccess: () -> Unit,
		onError: (String) -> Unit
	) {
		val companyInfo = CompanyModel(
			companyName = companyName,
			companyId = companyId,
			ownerUID = userId,
			bio = "",
			imageUrl = null
		)
		
		val companyAdmin = CompanyProfileModel(
			imageUrl = null,
			bio = "",
			status = UserStatus.ADMIN,
			id = userId,
			companyId = companyId
		)
		
		val profileModel = ProfileModel(
			email = email,
			firstName = firstName,
			lastName = lastName,
			companyList = listOf(companyId),
		)
		
		val companyRef = db.collection("companies").document(companyId)
		val userRef = db.collection("users").document(userId)
		val companyUserRef = companyRef.collection("users").document(userId)
		
		// Execute Firestore operations
		companyRef.set(companyInfo)
			.addOnSuccessListener {
				Log.d(TAG, "Company created: $companyInfo")
				// Update UserSession
				UserSession.setCompany(companyInfo)
				
				userRef.set(profileModel)
					.addOnSuccessListener {
						Log.d(TAG, "User profile created")
						// Update UserSession
						UserSession.setProfile(profileModel)
						_userCompanyList.value = profileModel.companyList
						
						companyUserRef.set(companyAdmin)
							.addOnSuccessListener {
								Log.d(TAG, "Company admin created")
								// Update UserSession
								UserSession.setCompanyProfile(companyAdmin)
								UserSession.setSelectedCompanyId(companyId)
								
								onSuccess()
							}
							.addOnFailureListener { e ->
								Log.e(TAG, "Failed to create company admin", e)
								onError("Failed to create company admin: ${e.message}")
							}
					}
					.addOnFailureListener { e ->
						Log.e(TAG, "Failed to create user profile", e)
						onError("Failed to create user profile: ${e.message}")
					}
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Failed to create company", e)
				onError("Failed to create company: ${e.message}")
			}
	}
	
	/**
	 * Joins a user to an existing company.
	 */
	fun joinCompany(companyId: String, userId: String) {
		val selectedCompany = _companies.value.find { it.companyId == companyId }
		selectedCompany?.let { company ->
			val isOwner = company.ownerUID == userId
			val status = if(isOwner) UserStatus.ADMIN else UserStatus.USER
			
			val companyProfile = CompanyProfileModel(
				imageUrl = null,
				bio = "No bio yet",
				status = status,
				id = userId,
				companyId = companyId
			)
			
			db.collection("users")
				.document(userId)
				.update("companyList", FieldValue.arrayUnion(companyId))
				.addOnSuccessListener {
					// Update ViewModel company list
					val currentList = _userCompanyList.value
					if (!currentList.contains(companyId)) {
						_userCompanyList.value = currentList + companyId
					}
				}
			
			db.collection("companies")
				.document(companyId)
				.collection("users")
				.document(userId)
				.set(companyProfile)
				.addOnSuccessListener {
					// Update UserSession
					UserSession.setCompanyProfile(companyProfile)
				}
			
			selectCompany(companyId, userId)
		}
	}
	
	/**
	 * Registers a user to an existing company.
	 * Ensures synchronization across Firestore and UserSession.
	 */
	fun registerUserToCompany(userId: String, companyId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
		if (userId.isEmpty()) {
			onError("User not logged in")
			return
		}
		
		// First check if company exists
		db.collection("companies").document(companyId).get()
			.addOnSuccessListener { companyDoc ->
				if (!companyDoc.exists()) {
					onError("Company does not exist")
					return@addOnSuccessListener
				}
				
				val companyName = companyDoc.getString("companyName") ?: ""
				
				// Get current user data
				db.collection("users").document(userId).get()
					.addOnSuccessListener { userDoc ->
						val email = userDoc.getString("email") ?: ""
						
						// Create company profile
						val companyProfileModel = CompanyProfileModel(
							id = userId,
							imageUrl = null,
							bio = "",
							status = UserStatus.USER,
							companyId = companyId
						)
						
						// Add user to company
						db.collection("companies")
							.document(companyId)
							.collection("users").document(userId)
							.set(companyProfileModel)
							.addOnSuccessListener {
								// Update user's company list
								updateUserCompanyList(userId, companyId)
								
								// Update UserSession
								UserSession.setCompanyProfile(companyProfileModel)
								
								// Fetch company data
								fetchCompanyData(companyId)
								
								onSuccess()
							}
							.addOnFailureListener { e ->
								Log.e(TAG, "Failed to add user to company", e)
								onError("Failed to add to company: ${e.message}")
							}
					}
					.addOnFailureListener { e ->
						onError("Failed to get user data: ${e.message}")
					}
			}
			.addOnFailureListener { e ->
				onError("Failed to verify company: ${e.message}")
			}
	}
	
	/**
	 * Updates a user's company list in Firestore and UserSession.
	 * This is crucial for keeping the company list synchronized.
	 */
	private fun updateUserCompanyList(userId: String, companyId: String) {
		Log.d(TAG, "Updating company list for user: $userId with company: $companyId")
		
		db.collection("users").document(userId).get()
			.addOnSuccessListener { userDoc ->
				val currentCompanyList = userDoc.get("companyList") as? List<String> ?: listOf()
				
				// Only add if not already in list
				if (!currentCompanyList.contains(companyId)) {
					val updatedList = currentCompanyList + companyId
					Log.d(TAG, "Adding company to list: $updatedList")
					
					// Update Firestore
					db.collection("users").document(userId)
						.update("companyList", updatedList)
						.addOnSuccessListener {
							Log.d(TAG, "Firestore updated with company list: $updatedList")
							
							// Update UserSession if profile exists
							val currentProfile = UserSession.profile.value
							if (currentProfile != null) {
								val updatedProfile = currentProfile.copy(companyList = updatedList)
								UserSession.setProfile(updatedProfile)
								
								// Update ViewModel state
								_userCompanyList.value = updatedList
								Log.d(TAG, "UserSession and ViewModel updated with company list")
							}
						}
						.addOnFailureListener { e ->
							Log.e(TAG, "Failed to update company list", e)
						}
				} else {
					Log.d(TAG, "Company already in list, no update needed")
				}
			}
	}
	
	//=== TEST FUNCTIONS ===//
	
	/**
	 * Adds a test user to a company (for development).
	 */
	fun testRegToCompany(companyId: String, userId: String, email: String, firstName: String, lastName: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
		// Add user to Firestore users collection if needed
		val profileModel = ProfileModel(
			email = email,
			firstName = firstName,
			lastName = lastName,
			companyList = listOf(companyId)
		)
		
		// Update user document
		db.collection("users").document(userId)
			.set(profileModel)
			.addOnSuccessListener {
				// Update UserSession
				UserSession.setProfile(profileModel)
				_userCompanyList.value = profileModel.companyList
				
				// Register to company
				registerUserToCompany(userId, companyId, onSuccess, onError)
			}
			.addOnFailureListener { e ->
				onError("Failed to create user profile: ${e.message}")
			}
	}
	
	/**
	 * Registers a new user to a test company (for development).
	 */
	fun registerToTestCompany(userId: String) {
		// Hardcoded company ID
		val companyId = "17c9dab0-e425-457a-b0d3-b3009ee81c27"
		
		// Update UserSession
		UserSession.setSelectedCompanyId(companyId)
		
		// Fetch company data
		db.collection("companies").document(companyId).get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val company = document.toObject(CompanyModel::class.java)
					company?.let {
						UserSession.setCompany(it)
					}
				}
			}
		
		// Create default company profile if needed
		val defaultCompanyProfile = CompanyProfileModel(
			id = userId,
			companyId = companyId,
			bio = "New user bio",
			status = UserStatus.USER
		)
		
		// Update Firestore with the user-company relationship
		db.collection("companies")
			.document(companyId)
			.collection("users")
			.document(userId)
			.set(defaultCompanyProfile)
			.addOnSuccessListener {
				// Set in UserSession once confirmed in Firestore
				UserSession.setCompanyProfile(defaultCompanyProfile)
				
				// Update user's companyList to include this company
				updateUserCompanyList(userId, companyId)
			}
		
		// Store as last selected company
		db.collection("users")
			.document(userId)
			.update("lastSelectedCompany", companyId)
	}
	
	//=== STORAGE HELPER METHODS ===//
	
	/**
	 * Gets stored company ID from local storage.
	 */
	private fun getStoredCompanyId(): String? {
		// Implement using SharedPreferences or DataStore
		return null // Replace with actual implementation
	}
	
	/**
	 * Stores company ID in local storage.
	 */
	private fun storeCompanyId(companyId: String) {
		// Implement using SharedPreferences or DataStore
	}
	
	/**
	 * Clears stored company ID from local storage.
	 */
	private fun clearStoredCompanyId() {
		// Implement using SharedPreferences or DataStore
	}
}