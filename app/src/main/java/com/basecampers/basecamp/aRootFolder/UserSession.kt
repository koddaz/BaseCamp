package com.basecampers.basecamp.aRootFolder

import com.basecampers.basecamp.tabs.profile.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
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
	
	/**
	 * Initialize the session with user ID.
	 * This is called when the user logs in.
	 */
	fun initialize(userId: String?) {
		if (userId != null) {
			_userId.value = userId
		} else {
			clearSession()
		}
	}
	
	/**
	 * Set user ID
	 */
	fun setUserId(userId: String?) {
		_userId.value = userId
	}
	
	/**
	 * Set user profile data
	 */
	fun setProfile(profile: ProfileModel) {
		_profile.value = profile
	}
	
	/**
	 * Set company data
	 */
	fun setCompany(company: CompanyModel) {
		_company.value = company
	}
	
	/**
	 * Set company profile data
	 */
	fun setCompanyProfile(companyProfile: CompanyProfileModel) {
		_companyProfile.value = companyProfile
	}
	
	/**
	 * Set selected company ID
	 */
	fun setSelectedCompanyId(companyId: String?) {
		_selectedCompanyId.value = companyId
	}
	
	/**
	 * Clear all session data
	 */
	fun clearSession() {
		_userId.value = null
		_profile.value = null
		_company.value = null
		_companyProfile.value = null
		_selectedCompanyId.value = null
	}
}