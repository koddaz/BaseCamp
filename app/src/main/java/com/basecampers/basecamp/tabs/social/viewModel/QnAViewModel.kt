package com.basecampers.basecamp.tabs.social.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.social.models.QnAItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for Q&A functionality
 */
class QnAViewModel : ViewModel() {
	private val TAG = "QnAViewModel"
	private val db = FirebaseFirestore.getInstance()
	
	// State for Q&A items
	private val _qnaItems = MutableStateFlow<List<QnAItem>>(emptyList())
	val qnaItems: StateFlow<List<QnAItem>> = _qnaItems.asStateFlow()
	
	// Loading state
	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
	
	// Search query
	private val _searchQuery = MutableStateFlow("")
	val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
	
	/**
	 * Updates search query
	 */
	fun updateSearchQuery(query: String) {
		_searchQuery.value = query
		// If search is implemented, add filtering logic here
	}
	
	/**
	 * Fetches Q&A items for current company
	 */
	fun fetchQnAItems() {
		val companyId = UserSession.selectedCompanyId.value
		if (companyId.isNullOrEmpty()) {
			Log.e(TAG, "No company selected")
			return
		}
		
		_isLoading.value = true
		
		db.collection("companies")
			.document(companyId)
			.collection("qna")
			.get()
			.addOnSuccessListener { snapshot ->
				val items = snapshot.documents.mapNotNull { doc ->
					try {
						// Create QnAItem with the document ID
						QnAItem(
							id = doc.id,
							question = doc.getString("question") ?: "",
							answer = doc.getString("answer") ?: "",
							isPublished = doc.getBoolean("published") ?: true
						)
					} catch (e: Exception) {
						Log.e(TAG, "Error parsing QnA item", e)
						null
					}
				}
				_qnaItems.value = items
				_isLoading.value = false
				Log.d(TAG, "Fetched ${items.size} Q&A items")
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Error fetching Q&A items", e)
				_isLoading.value = false
			}
	}
	
	/**
	 * Adds a new Q&A item
	 */
	fun addQnAItem(question: String, answer: String, isPublished: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
		val companyId = UserSession.selectedCompanyId.value
		if (companyId.isNullOrEmpty()) {
			onError("No company selected")
			return
		}
		
		val item = mapOf(
			"question" to question,
			"answer" to answer,
			"published" to isPublished
		)
		
		db.collection("companies")
			.document(companyId)
			.collection("qna")
			.add(item)
			.addOnSuccessListener { docRef ->
				Log.d(TAG, "Added Q&A item with ID: ${docRef.id}")
				// Refresh the list
				fetchQnAItems()
				onSuccess()
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Error adding Q&A item", e)
				onError(e.message ?: "Unknown error")
			}
	}
	
	/**
	 * Updates an existing Q&A item
	 */
	fun updateQnAItem(id: String, question: String, answer: String, isPublished: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
		val companyId = UserSession.selectedCompanyId.value
		if (companyId.isNullOrEmpty()) {
			onError("No company selected")
			return
		}
		
		val updates = mapOf(
			"question" to question,
			"answer" to answer,
			"published" to isPublished
		)
		
		db.collection("companies")
			.document(companyId)
			.collection("qna")
			.document(id)
			.update(updates)
			.addOnSuccessListener {
				Log.d(TAG, "Updated Q&A item: $id")
				// Refresh the list
				fetchQnAItems()
				onSuccess()
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Error updating Q&A item", e)
				onError(e.message ?: "Unknown error")
			}
	}
	
	/**
	 * Deletes a Q&A item
	 */
	fun deleteQnAItem(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
		val companyId = UserSession.selectedCompanyId.value
		if (companyId.isNullOrEmpty()) {
			onError("No company selected")
			return
		}
		
		db.collection("companies")
			.document(companyId)
			.collection("qna")
			.document(id)
			.delete()
			.addOnSuccessListener {
				Log.d(TAG, "Deleted Q&A item: $id")
				// Refresh the list
				fetchQnAItems()
				onSuccess()
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Error deleting Q&A item", e)
				onError(e.message ?: "Unknown error")
			}
	}
	
	/**
	 * Toggles the published state of a Q&A item
	 */
	fun toggleQnAPublished(id: String, isPublished: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
		val companyId = UserSession.selectedCompanyId.value
		if (companyId.isNullOrEmpty()) {
			onError("No company selected")
			return
		}
		
		db.collection("companies")
			.document(companyId)
			.collection("qna")
			.document(id)
			.update("published", isPublished)
			.addOnSuccessListener {
				Log.d(TAG, "Updated Q&A publish state: $id to $isPublished")
				// Refresh the list
				fetchQnAItems()
				onSuccess()
			}
			.addOnFailureListener { e ->
				Log.e(TAG, "Error updating Q&A publish state", e)
				onError(e.message ?: "Unknown error")
			}
	}
}