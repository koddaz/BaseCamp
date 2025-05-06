package com.basecampers.basecamp.aRootFolder

import android.util.Log
import com.basecampers.basecamp.company.models.CompanyModel
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global session manager that maintains the current user data.
 * Acts as a central container for profile and company data.
 */
object UserSession {
	private val TAG = "UserSession"
	
	// Application-wide coroutine scope
	private val sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
	
	// User ID from auth
	private val _userId = MutableStateFlow<String?>(null)
	val userId = _userId.asStateFlow()
	
	// User profile data
	private val _profile = MutableStateFlow<ProfileModel?>(null)
	val profile = _profile.asStateFlow()
	
	// Company data
	private val _company = MutableStateFlow<CompanyModel?>(null)
	val company = _company.asStateFlow()
	
	// User's company-specific profile
	private val _companyProfile = MutableStateFlow<CompanyProfileModel?>(null)
	val companyProfile = _companyProfile.asStateFlow()
	
	// Selected company ID (for reference only)
	private val _selectedCompanyId = MutableStateFlow<String?>(null)
	val selectedCompanyId = _selectedCompanyId.asStateFlow()

	private val _selectedCompanyName = MutableStateFlow<String?>(null)
	val selectedCompanyName = _selectedCompanyName.asStateFlow()

	private val _companyNames = MutableStateFlow<Map<String, String>>(emptyMap())
	val companyNames = _companyNames.asStateFlow()
	/**
	 * Initialize the session with user ID.
	 * This is called when the user logs in.
	 */
	fun initialize(userId: String?) {
		Log.d(TAG, "Initializing session with userId: $userId")
		if (userId != null) {
			_userId.value = userId
			Log.d(TAG, "UserId set to: $userId")
		} else {
			Log.d(TAG, "UserId is null, clearing session")
			clearSession()
		}
	}

	fun updateCompanyName(companyId: String, companyName: String) {
		val currentMap = _companyNames.value.toMutableMap()
		currentMap[companyId] = companyName
		_companyNames.value = currentMap

		// Update selected company name if this is the selected company
		if (companyId == _selectedCompanyId.value) {
			_selectedCompanyName.value = companyName
		}
	}

	// Method to set all company names at once
	fun setCompanyNames(namesMap: Map<String, String>) {
		_companyNames.value = namesMap
	}
	/**
	 * Set user ID
	 */
	fun setUserId(userId: String?) {
		Log.d(TAG, "Setting userId: $userId")
		_userId.value = userId
	}
	
	/**
	 * Set user profile data
	 */
	fun setProfile(profile: ProfileModel) {
		Log.d(TAG, "Setting profile: ${profile.email}, firstName: ${profile.firstName}, lastName: ${profile.lastName}")
		_profile.value = profile
	}
	
	/**
	 * Set company data
	 */
	fun setCompany(company: CompanyModel) {
		Log.d(TAG, "Setting company: ${company.companyName}, id: ${company.companyId}")
		_company.value = company
	}
	
	/**
	 * Set company profile data
	 */
	fun setCompanyProfile(companyProfile: CompanyProfileModel) {
		Log.d(TAG, "Setting companyProfile - id: ${companyProfile.id}, status: ${companyProfile.status}")
		_companyProfile.value = companyProfile
	}
	
	/**
	 * Set selected company ID
	 */
	fun setSelectedCompanyId(companyId: String?) {
		Log.d(TAG, "Setting selectedCompanyId: $companyId")
		_selectedCompanyId.value = companyId
	}

	/**
	 * Clear all session data
	 */
	fun clearSession() {
		Log.d(TAG, "Clearing entire session")
		_userId.value = null
		_profile.value = null
		_company.value = null
		_companyProfile.value = null
		_selectedCompanyId.value = null
		Log.d(TAG, "Session cleared")
	}
	
	/**
	 * Log current session state
	 */
	fun logSessionState() {
		Log.d(TAG, "Current Session State:")
		Log.d(TAG, "- userId: ${_userId.value}")
		Log.d(TAG, "- profile: ${_profile.value?.email}, ${_profile.value?.firstName} ${_profile.value?.lastName}")
		Log.d(TAG, "- company: ${_company.value?.companyName} (${_company.value?.companyId})")
		Log.d(TAG, "- companyProfile status: ${_companyProfile.value?.status}")
		Log.d(TAG, "- selectedCompanyId: ${_selectedCompanyId.value}")
	}
}
