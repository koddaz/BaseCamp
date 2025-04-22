package com.basecampers.basecamp.aRootFolder
import android.content.Context
import android.content.SharedPreferences

/**
 * Utility class for storing app preferences
 */
class AppPreferences(context: Context) {
	private val preferences: SharedPreferences = context.getSharedPreferences(
		"basecamp_preferences",
		Context.MODE_PRIVATE
	)
	
	companion object {
		private const val KEY_SELECTED_COMPANY_ID = "selected_company_id"
	}
	
	/**
	 * Stores selected company ID
	 */
	fun saveSelectedCompanyId(companyId: String) {
		preferences.edit().putString(KEY_SELECTED_COMPANY_ID, companyId).apply()
	}
	
	/**
	 * Retrieves selected company ID
	 */
	fun getSelectedCompanyId(): String? {
		return preferences.getString(KEY_SELECTED_COMPANY_ID, null)
	}
	
	/**
	 * Clears selected company ID
	 */
	fun clearSelectedCompanyId() {
		preferences.edit().remove(KEY_SELECTED_COMPANY_ID).apply()
	}
}