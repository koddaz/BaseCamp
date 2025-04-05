package com.example.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
import com.example.basecamp.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.text.get
import kotlin.text.set
import kotlin.toString

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _bookingItems = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItems: StateFlow<List<BookingItem>> = _bookingItems

    // Map to store items for each category
    private val _categoryItems = MutableStateFlow<Map<String, List<BookingItem>>>(emptyMap())
    val categoryItems: StateFlow<Map<String, List<BookingItem>>> = _categoryItems

    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user


    fun setUser(userModel: UserModel) {
        _user.value = userModel
        // Fetch categories whenever the user is set
        retrieveCategories()
    }

    private fun getCompanyName(): String? {
        return _user.value?.companyName
    }

    fun addBookingCategory(bookingCategory: BookingCategories) {
        val companyName = getCompanyName() ?: return

        // Use the ID from the model as the document ID
        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .document(bookingCategory.id)  // Use the ID from the model
            .set(bookingCategory)  // Use set instead of add
            .addOnSuccessListener {
                retrieveCategories()
            }
    }

    fun addBookingItem(bookingItem: BookingItem, selectedCategory: String) {
        val companyName = getCompanyName() ?: return

        // Convert the integer ID to string for Firestore document ID
        val itemId = bookingItem.id.toString()

        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .document(selectedCategory)
            .collection("items")
            .document(itemId)  // Use the ID from the model
            .set(bookingItem)  // Use set instead of add
            .addOnSuccessListener {
                retrieveBookingItems(selectedCategory)
            }
    }

    fun addBookingExtra(bookingItem: BookingItem, bookingExtra: BookingExtra, selectedCategory: String) {
        val companyName = getCompanyName() ?: return
        val extraId = bookingExtra.id.toString()
        val itemId = bookingItem.id.toString()

        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .document(selectedCategory)
            .collection("items")
            .document(itemId)
            .collection("extras")
            .document(extraId)
            .set(bookingExtra)


    }
    fun retrieveBookingItems(selectedCategory: String) {
        val companyName = getCompanyName() ?: return

        // Fix the path to match where items are actually stored
        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .document(selectedCategory)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val itemList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val quantity = doc.getLong("quantity")?.toInt() ?: 1
                    val pricePerDay = doc.getDouble("pricePerDay") ?: 0.0
                    val createdBy = doc.getString("createdBy") ?: ""
                    BookingItem(
                        id = id,
                        pricePerDay = pricePerDay,
                        name = name,
                        info = info,
                        quantity = quantity,
                        createdBy = createdBy,
                    )
                }
                // Update both the general bookingItems and category-specific items
                _bookingItems.value = itemList

                // Update the map with items for this category
                val updatedMap = _categoryItems.value.toMutableMap()
                updatedMap[selectedCategory] = itemList
                _categoryItems.value = updatedMap
            }
    }



    fun retrieveCategories() {
        val companyName = getCompanyName() ?: return

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
            }
    }
}