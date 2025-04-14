package com.basecampers.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserBookingViewModel : ViewModel() {
    
    val db = Firebase.firestore
    
    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)
    
    private val _bookingItemsList = MutableStateFlow<List<BookingItems>>(emptyList())
    val bookingItemsList: StateFlow<List<BookingItems>> = _bookingItemsList
    
    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories
    
    private val _formattedDateRange = MutableStateFlow<String>("")
    val formattedDateRange: StateFlow<String> = _formattedDateRange
    
    private val _selectedBookingItem = MutableStateFlow<BookingItems?>(null)
    val selectedBookingItem: StateFlow<BookingItems?> = _selectedBookingItem
    
    private val _selectedExtraItems = MutableStateFlow<List<ExtraItems>>(emptyList())
    val seleectedExtraItems: StateFlow<List<ExtraItems>> = _selectedExtraItems
    
    val currentUserId = Firebase.auth.currentUser?.uid
    
    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user
    
    
    fun setUser(userModel: UserModel) {
        _user.value = userModel
        retrieveCategories()
    }
    
    private fun getCompanyName(): String? {
        return _user.value?.companyName
    }
    
    fun retrieveCategoriesAndItems() {
        val companyName = getCompanyName() ?: run {
            // Fallback to existing method if company name not available
            val currentUserUid = Firebase.auth.currentUser?.uid
            if (currentUserUid != null) {
                db.collection("users")
                    .document(currentUserUid)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val docCompanyName = userDoc.getString("companyName")
                        if (docCompanyName != null) {
                            fetchCategoriesAndItems(docCompanyName)
                        }
                    }
            }
            return
        }
        
        fetchCategoriesAndItems(companyName)
    }
    
    private fun fetchCategoriesAndItems(companyName: String) {
        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val createdBy = doc.getString("createdBy") ?: ""
                    BookingCategories(id, name, info, createdBy)
                }
                
                _categories.value = categoryList
                
                // Get items for all categories
                if (categoryList.isNotEmpty()) {
                    val firstCategory = categoryList.first()
                    fetchItemsForCategory(firstCategory.id, companyName)
                }
            }
    }
    
    private fun fetchItemsForCategory(categoryId: String, companyName: String) {
        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(categoryId)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val itemsList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val price = doc.getDouble("pricePerDay") ?: 0.0
                    
                    BookingItems(
                        id = id,
                        name = name,
                        info = info,
                        price = price
                    )
                }
                _bookingItemsList.value = itemsList
            }
    }
    fun retrieveCategories() {
        val companyName = getCompanyName()
        if (companyName != null) {
            retrieveCategoriesFromCompany(companyName)
        } else {
            // Fallback to existing method
            val currentUserUid = Firebase.auth.currentUser?.uid
            if (currentUserUid != null) {
                db.collection("users")
                    .document(currentUserUid)
                    .get()
                    .addOnSuccessListener { userDoc ->
                        val docCompanyName = userDoc.getString("companyName")
                        if (docCompanyName != null) {
                            retrieveCategoriesFromCompany(docCompanyName)
                        }
                    }
            }
        }
    }
    
    
    // Your existing method with user parameter
    fun retrieveCategories(user: UserModel) {
        retrieveCategoriesFromCompany(user.companyName)
    }
    
    // Helper method to avoid code duplication
    private fun retrieveCategoriesFromCompany(companyName: String) {
        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    BookingCategories(id, name, info)
                }
                _categories.value = categoryList
            }
    }
    
    fun retrieveBookingItems(categoryId: String) {
        val companyName = getCompanyName() ?: return
        
        db.collection("companies")
            .document(companyName)  // Use actual company name instead of hardcoded "companyName"
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(categoryId)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val itemsList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getLong("id")?.toInt() ?: 0
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val price = doc.getDouble("pricePerDay") ?: 0.0
                    
                    BookingItems(
                        id = id,
                        name = name,
                        info = info,
                        price = price
                    )
                }
                _bookingItemsList.value = itemsList
            }
    }
    
    fun loadItemsForCategory(category: BookingCategories) {
        retrieveBookingItems(category.id)
    }
    
    
    
    fun addExtraItem(item: ExtraItems) {
        _selectedExtraItems.value = _selectedExtraItems.value + item
    }
    
    fun removeExtraItem(item: ExtraItems) {
        _selectedExtraItems.value = _selectedExtraItems.value - item
    }
    
    fun updateSelectedDateRange(start: Long?, end: Long? ) {
        _selectedDateRange.value = Pair(start, end)
        _startDate.value = start
        _endDate.value = end
        formatDateRange(_startDate.value, _endDate.value)
    }
    
    fun formatDateRange(startDate: Long?, endDate: Long?) {
        val startFormatted = startDate?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "No start date"
        
        val endFormatted = endDate?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "No end date"
        
        _formattedDateRange.value = "$startFormatted - $endFormatted"
    }
    
    fun setSelectedBookingItem(item: BookingItems?) {
        _selectedBookingItem.value = item
        
    }
    
    fun removeSelectedBookingItem() {
        _selectedBookingItem.value = null
    }
    
    fun createBooking(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUserUid = Firebase.auth.currentUser?.uid
        if (currentUserUid == null) {
            onFailure(Exception("User not logged in"))
            return
        }
        
        val selectedItem = selectedBookingItem.value
        if (selectedItem == null) {
            onFailure(Exception("No booking item selected"))
            return
        }
        
        val booking = hashMapOf(
            "bookingItem" to mapOf(
                "id" to selectedItem.id,
                "name" to selectedItem.name,
                "price" to selectedItem.price
            ),
            "extraItems" to seleectedExtraItems.value.map { item ->
                mapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "price" to item.price
                )
            },
            "timestamp" to com.google.firebase.Timestamp.now(),
            "totalPrice" to (selectedItem.price + seleectedExtraItems.value.sumOf { it.price }),
            "userId" to currentUserUid // Add user reference
        )
        
        // Add to company bookings
        val companyBookingRef = db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("currentBookings")
            .collection("bookings")
        
        // Add to user bookings
        val userBookingRef = db.collection("companies")
            .document("companyName")
            .collection("users")
            .document(currentUserUid)
            .collection("bookings")
        
        // Use a batch write to ensure both operations complete together
        val batch = db.batch()
        
        // Create document references with the same ID
        val newBookingRef = companyBookingRef.document()
        val bookingId = newBookingRef.id
        
        batch.set(newBookingRef, booking)
        batch.set(userBookingRef.document(bookingId), booking)
        
        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
    
}