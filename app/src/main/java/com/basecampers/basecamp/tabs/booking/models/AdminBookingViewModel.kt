package com.basecampers.basecamp.tabs.booking.models


import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore

    // Booking item
    private val _itemName = MutableStateFlow("")
    private val _itemInfo = MutableStateFlow("")
    private val _itemPrice = MutableStateFlow("")
    private val _itemQuantity = MutableStateFlow("")

    val itemName: StateFlow<String> = _itemName
    val itemInfo: StateFlow<String> = _itemInfo
    val itemPrice: StateFlow<String> = _itemPrice
    val itemQuantity: StateFlow<String> = _itemQuantity

    // Extra item
    private val _extraName = MutableStateFlow("")
    private val _extraInfo = MutableStateFlow("")
    private val _extraPrice = MutableStateFlow("")

    val extraName: StateFlow<String> = _extraName
    val extraInfo: StateFlow<String> = _extraInfo
    val extraPrice: StateFlow<String> = _extraPrice



    private val _bookingItems = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItems: StateFlow<List<BookingItem>> = _bookingItems

    private val _categoryItems = MutableStateFlow<Map<String, List<BookingItem>>>(emptyMap())
    val categoryItems: StateFlow<Map<String, List<BookingItem>>> = _categoryItems

    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories

    private val _user = MutableStateFlow<CompanyProfileModel?>(null)
    val user: StateFlow<CompanyProfileModel?> = _user

    private val _selectedCategoryId = MutableStateFlow<String>("")
    val selectedCategoryId = _selectedCategoryId.asStateFlow()

    private val _selectedItemId = MutableStateFlow<String>("")
    val selectedItemId = _selectedItemId.asStateFlow()

    // Add this function
    fun setSelectedCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        // Load items for this category
        retrieveBookingItems(categoryId)
    }

    fun setSelectedItemId(itemId: String) {
        _selectedItemId.value = itemId
    }

    private fun getCompanyId(): String? {
        return _user.value?.companyId
    }

    fun setUser(companyProfileModel: CompanyProfileModel) {
        _user.value = companyProfileModel
        // Fetch categories whenever the user is set
        retrieveCategories()
    }



    fun setItems(
        name: String = "",
        info: String = "",
        price: String = "",
        quantity: String = "",
    ) {
        _itemName.value = name
        _itemInfo.value = info
        _itemPrice.value = price
        _itemQuantity.value = quantity
    }

    fun setExtras(
        name: String = "",
        info: String = "",
        price: String = "",
    ) {
        _extraName.value = name
        _extraInfo.value = info
        _extraPrice.value = price
    }

    fun addBookingCategory(bookingCategory: BookingCategories) {
        val companyId = getCompanyId() ?: return

        val categoryRef = db
            .collection("companies").document(companyId)
            .collection("categories").document(bookingCategory.id)

        categoryRef.set(bookingCategory)
            .addOnSuccessListener {
                retrieveCategories()
            }
            .addOnFailureListener { e ->
                Log.e("AdminBookingViewModel", "Failed to add category: ${e.message}")
            }
    }

    fun addBookingItem(bookingItem: BookingItem, selectedCategory: String) {
        val companyId = getCompanyId() ?: return

        if (selectedCategory.isEmpty()) {
            Log.e("AdminBookingViewModel", "Selected category is empty")
            return
        }

        try {
            val documentRef = db
                .collection("companies").document(companyId)
                .collection("categories").document(selectedCategory)
                .collection("bookings").document(bookingItem.id)


            documentRef.set(bookingItem)
                .addOnSuccessListener {
                    Log.d("AdminBookingViewModel", "Item added successfully")
                    // Refresh the items list after adding a new item
                    retrieveBookingItems(selectedCategory)
                }
                .addOnFailureListener { e ->
                    Log.e("AdminBookingViewModel", "Failed to add item: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("AdminBookingViewModel", "Error adding booking item", e)
        }
    }

    fun addBookingExtra(bookingItem: BookingItem, bookingExtra: BookingExtra, selectedCategory: String) {
        val companyId = getCompanyId() ?: return

        if (selectedCategory.isEmpty()) {
            Log.e("AdminBookingViewModel", "Selected category is empty")
            return
        }

        if (bookingItem.id.isEmpty()) {
            Log.e("AdminBookingViewModel", "BookingItem ID is empty")
            return
        }

        Log.d("AdminBookingViewModel", "Adding extra to category: $selectedCategory")
        Log.d("AdminBookingViewModel", "BookingItem ID: ${bookingItem.id}")
        Log.d("AdminBookingViewModel", "BookingExtra ID: ${bookingExtra.id}")

        try {
            val documentRef = db
                .collection("companies").document(companyId)
                .collection("categories").document(selectedCategory)
                .collection("bookings").document(bookingItem.id)
                .collection("extras").document(bookingExtra.id)

            documentRef.set(bookingExtra)
                .addOnSuccessListener {
                    Log.d("AdminBookingViewModel", "Extra added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("AdminBookingViewModel", "Failed to add extra: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("AdminBookingViewModel", "Error adding booking extra", e)
        }
    }

    fun retrieveBookingItems(selectedCategory: String) {
        val companyId = getCompanyId() ?: return

        db
            .collection("companies").document(companyId)
            .collection("categories").document(selectedCategory)
            .collection("bookings").get()
            .addOnSuccessListener { snapshot ->
                val itemList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val quantity = doc.getString("quantity") ?: ""
                    val pricePerDay = doc.getString("pricePerDay") ?: ""
                    val createdBy = doc.getString("createdBy") ?: ""
                    val categoryId = doc.getString("categoryId") ?: ""
                    BookingItem(
                        id = id,
                        pricePerDay = pricePerDay,
                        name = name,
                        info = info,
                        quantity = quantity,
                        createdBy = createdBy,
                        categoryId = categoryId
                    )
                }
                _bookingItems.value = itemList

                val updatedMap = _categoryItems.value.toMutableMap()
                updatedMap[selectedCategory] = itemList
                _categoryItems.value = updatedMap
            }
    }

    fun retrieveCategories() {
        val companyId = getCompanyId() ?: return

        db
            .collection("companies").document(companyId)
            .collection("categories").get()
            .addOnSuccessListener { snapshot ->
                val categoryList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val createdBy = doc.getString("createdBy") ?: ""
                    BookingCategories(id, name, info, createdBy)
                }
                _categories.value = categoryList
            }
    }
}