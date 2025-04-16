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







    private val _bookingExtras = MutableStateFlow<List<BookingExtra>>(emptyList())
    val bookingExtras: StateFlow<List<BookingExtra>> = _bookingExtras

    private val _bookingItems = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItems: StateFlow<List<BookingItem>> = _bookingItems

    private val _categoryItems = MutableStateFlow<Map<String, List<BookingItem>>>(emptyMap())
    val categoryItems: StateFlow<Map<String, List<BookingItem>>> = _categoryItems

    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories

    private val _user = MutableStateFlow<CompanyProfileModel?>(null)
    val user: StateFlow<CompanyProfileModel?> = _user


    private val _selectedItem = MutableStateFlow<BookingItem?>(null)
    val selectedItem: StateFlow<BookingItem?> = _selectedItem

    private val _selectedCategory = MutableStateFlow<BookingCategories?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedExtraItem = MutableStateFlow<BookingExtra?>(null)
    val selectedExtraItem = _selectedExtraItem.asStateFlow()

    private fun getCompanyId(): String? {
        return _user.value?.companyId
    }

    fun setUser(companyProfileModel: CompanyProfileModel) {
        _user.value = companyProfileModel
        // Fetch categories whenever the user is set
        retrieveCategories()
    }

    fun addExtraList(extraList: List<BookingExtra>) {
        _bookingExtras.value = extraList + _bookingExtras.value
    }

    fun updateExtraValue(
        id: String = _selectedExtraItem.value?.id ?: "",
        name: String = _selectedExtraItem.value?.name ?: "",
        info: String = _selectedExtraItem.value?.info ?: "",
        price: String = _selectedExtraItem.value?.price ?: ""
    ) {
        _selectedExtraItem.value = BookingExtra(
            id = id.ifEmpty { "${System.currentTimeMillis()}_${(1000..9999).random()}" },
            name = name,
            info = info,
            price = price
        )
    }


    fun updateCategoriesValues(
        id: String = _selectedCategory.value?.id ?: "",
        name: String = _selectedCategory.value?.name ?: "",
        info: String = _selectedCategory.value?.info ?: "",
        createdBy: String = _selectedCategory.value?.createdBy ?: ""
    ) {
        _selectedCategory.value = BookingCategories(
            id = id.ifEmpty { "${System.currentTimeMillis()}_${(1000..9999).random()}" },
            name = name,
            info = info,
            createdBy = createdBy
        )
    }

    fun updateBookingItemValues(
        id: String = _selectedItem.value?.id ?: "",
        name: String = _selectedItem.value?.name ?: "",
        info: String = _selectedItem.value?.info ?: "",
        price: String = _selectedItem.value?.pricePerDay ?: "",
        quantity: String = _selectedItem.value?.quantity ?: "",
        categoryId: String = _selectedItem.value?.categoryId ?: "",
        createdBy: String = _selectedItem.value?.createdBy ?: ""
    ) {
        _selectedItem.value = BookingItem(
            id = id.ifEmpty { "${System.currentTimeMillis()}_${(1000..9999).random()}" },
            name = name,
            info = info,
            pricePerDay = price,
            quantity = quantity,
            categoryId = categoryId,
            createdBy = createdBy
        )
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

    fun retrieveBookingExtras(categoryId: String, bookingItemId: String) {
        val companyId = getCompanyId() ?: return

        db.collection("companies").document(companyId)
            .collection("categories").document(categoryId)
            .collection("bookings").document(bookingItemId)
            .collection("extras").get()
            .addOnSuccessListener { snapshot ->
                val extrasList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val price = doc.getString("price") ?: ""
                    BookingExtra(id, price, name, info)
                }
                _bookingExtras.value = extrasList
            }
    }

    // Function to delete an extra
    fun deleteBookingExtra(categoryId: String, bookingItemId: String, extraId: String) {
        val companyId = getCompanyId() ?: return

        db.collection("companies").document(companyId)
            .collection("categories").document(categoryId)
            .collection("bookings").document(bookingItemId)
            .collection("extras").document(extraId)
            .delete()
            .addOnSuccessListener {
                retrieveBookingExtras(categoryId, bookingItemId)
            }
    }


}