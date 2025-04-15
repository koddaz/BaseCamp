package com.basecampers.basecamp.company

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore

class CompanyViewModel : ViewModel() {
	private val _hasSelectedCompany = MutableStateFlow(false)
	val hasSelectedCompany: StateFlow<Boolean> = _hasSelectedCompany.asStateFlow()
	
	private val _currentCompanyId = MutableStateFlow<String?>(null)
	val currentCompanyId: StateFlow<String?> = _currentCompanyId.asStateFlow()
	
	private val db = FirebaseFirestore.getInstance()
	
	suspend fun checkSelectedCompany(userId: String) {
		// Check if user has a selected company in local storage/preferences first
		val storedCompanyId = getStoredCompanyId()
		
		if (storedCompanyId != null) {
			_currentCompanyId.value = storedCompanyId
			_hasSelectedCompany.value = true
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
					} else {
						_hasSelectedCompany.value = false
					}
				}
		} catch (e: Exception) {
			_hasSelectedCompany.value = false
		}
	}
	
	fun selectCompany(companyId: String, userId: String) {
		_currentCompanyId.value = companyId
		_hasSelectedCompany.value = true
		
		// Store in preferences
		storeCompanyId(companyId)
		
		// Update in Firestore
		db.collection("users")
			.document(userId)
			.update("lastSelectedCompany", companyId)
	}
	
	fun clearSelectedCompany() {
		_currentCompanyId.value = null
		_hasSelectedCompany.value = false
		clearStoredCompanyId()
	}
	
	// Helper methods for local storage
	private fun getStoredCompanyId(): String? {
		// Implement using SharedPreferences or DataStore
		return null // Replace with actual implementation
	}
	
	private fun storeCompanyId(companyId: String) {
		// Implement using SharedPreferences or DataStore
	}
	
	private fun clearStoredCompanyId() {
		// Implement using SharedPreferences or DataStore
	}
}