
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.profile.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.profile.models.UserStatus
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URL
import java.util.UUID

class CompanyViewModel : ViewModel() {
	private val _hasSelectedCompany = MutableStateFlow(false)
	val hasSelectedCompany: StateFlow<Boolean> = _hasSelectedCompany.asStateFlow()
	
	private val _currentCompanyId = MutableStateFlow<String?>(null)
	val currentCompanyId: StateFlow<String?> = _currentCompanyId.asStateFlow()

	private val _companies = MutableStateFlow<List<CompanyModel>>(emptyList())
	val companies: StateFlow<List<CompanyModel>> = _companies.asStateFlow()
	
	private val db = FirebaseFirestore.getInstance()

	init {
	    fetchCompanies()
	}
	
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

	fun joinCompany(companyId : String, userId : String) {
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

			db.collection("companies")
				.document(companyId)
				.collection("users")
				.document(userId)
				.set(companyProfile)

			selectCompany(companyId, userId)
		}
	}

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
				userRef.update("companyList", FieldValue.arrayUnion(companyId))
					.addOnSuccessListener {
						companyUserRef.set(companyAdmin)
							.addOnSuccessListener {
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
	
	
	// Add these helper methods to fetch company data for UserSession
	private fun fetchCompanyData(companyId: String) {
		db.collection("companies").document(companyId).get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val company = document.toObject(CompanyModel::class.java)
					company?.let {
						UserSession.setCompany(it)
					}
				}
			}
	}
	
	private fun fetchCompanyProfileData(userId: String, companyId: String) {
		db.collection("companies")
			.document(companyId)
			.collection("users")
			.document(userId)
			.get()
			.addOnSuccessListener { document ->
				if (document.exists()) {
					val companyProfile = document.toObject(CompanyProfileModel::class.java)
					companyProfile?.let {
						UserSession.setCompanyProfile(it)
					}
				}
			}
	}
}