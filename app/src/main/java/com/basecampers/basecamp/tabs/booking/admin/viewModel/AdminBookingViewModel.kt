package com.basecampers.basecamp.tabs.booking.admin.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore
    val userId = UserSession.userId
    val currentCompanyId = UserSession.selectedCompanyId.value

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


    init {
        viewModelScope.launch {
            retrieveCategories()
        }
    }

    fun addExtraList(extraList: List<BookingExtra>) {
        _bookingExtras.value = extraList + _bookingExtras.value
    }

    fun setSelectedItem(item: BookingItem) {
        _selectedItem.value = item
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

        val categoryRef = db
            .collection("companies").document(currentCompanyId.toString())
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
        db
            .collection("companies").document(currentCompanyId.toString())
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

        if (selectedCategory.isEmpty()) {
            Log.e("AdminBookingViewModel", "Selected category is empty")
            return
        }

        try {
            val documentRef = db
                .collection("companies").document(currentCompanyId.toString())
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


    fun editBooking(bookingItem: BookingItem, category: BookingCategories, extraItems: List<BookingExtra>) {


    }

    fun addBookingWithExtra(bookingItem: BookingItem, bookingExtras: List<BookingExtra>, selectedCategory: String) {

        if (selectedCategory.isEmpty()) {
            Log.e("AdminBookingViewModel", "Selected category is empty")
            return
        }

        try {
            val batch = db.batch()

            val itemRef = db
                .collection("companies").document(currentCompanyId.toString())
                .collection("categories").document(selectedCategory)
                .collection("bookings").document(bookingItem.id)

            // Add the booking item
            batch.set(itemRef, bookingItem)

            // Add each extra individually
            bookingExtras.forEach { extra ->
                // Use extra's ID if available, otherwise generate a new document ID
                val extraRef = if (extra.id.isNotBlank()) {
                    itemRef.collection("extras").document(extra.id)
                } else {
                    itemRef.collection("extras").document()
                }
                batch.set(extraRef, extra) // Save the individual extra
            }

            batch.commit()
                .addOnSuccessListener {
                    Log.d("AdminBookingViewModel", "Item and ${bookingExtras.size} extras added successfully")
                    retrieveBookingItems(selectedCategory)
                    retrieveBookingExtras(selectedCategory, bookingItem.id)
                }
                .addOnFailureListener { e ->
                    Log.e("AdminBookingViewModel", "Failed to add item and extras: ${e.message}")
                }

        } catch (e: Exception) {
            Log.e("AdminBookingViewModel", "Error adding booking item and extras", e)
        }
    }

    fun loadAllBookingItems() {
        categories.value.forEach { category ->
            retrieveBookingItems(category.id)
        }
    }

    fun addBookingExtra(bookingItem: BookingItem, bookingExtras: List<BookingExtra>, selectedCategory: String) {

        if (selectedCategory.isEmpty() || bookingItem.id.isEmpty()) {
            Log.e("AdminBookingViewModel", "Category or bookingItem ID is empty")
            return
        }

        try {
            val batch = db.batch()
            val baseRef = db
                .collection("companies").document(currentCompanyId.toString())
                .collection("categories").document(selectedCategory)
                .collection("bookings").document(bookingItem.id)
                .collection("extras")

            bookingExtras.forEach { extra ->
                val docRef = if (extra.id.isNotBlank()) {
                    baseRef.document(extra.id)
                } else {
                    baseRef.document()
                }
                batch.set(docRef, extra)
            }

            batch.commit()
                .addOnSuccessListener {
                    Log.d("AdminBookingViewModel", "${bookingExtras.size} extras added successfully")
                    retrieveBookingExtras(selectedCategory, bookingItem.id)
                }
                .addOnFailureListener { e ->
                    Log.e("AdminBookingViewModel", "Failed to add extras: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("AdminBookingViewModel", "Error adding booking extras", e)
        }
    }

    fun retrieveBookingItems(selectedCategory: String) {

        db
            .collection("companies").document(currentCompanyId.toString())
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

        db.collection("companies").document(currentCompanyId.toString())
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

        db.collection("companies").document(currentCompanyId.toString())
            .collection("categories").document(categoryId)
            .collection("bookings").document(bookingItemId)
            .collection("extras").document(extraId)
            .delete()
            .addOnSuccessListener {
                retrieveBookingExtras(categoryId, bookingItemId)
            }
    }


}