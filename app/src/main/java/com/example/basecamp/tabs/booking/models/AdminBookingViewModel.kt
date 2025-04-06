package com.example.basecamp.tabs.booking.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.basecamp.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.text.get
import kotlin.text.set
import kotlin.text.toInt
import kotlin.toString

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore

    private val _bookingItems = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItems: StateFlow<List<BookingItem>> = _bookingItems

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

        val categoryRef = db.collection("companies").document(companyName)
            .collection("bookings").document("categories")
            .collection("items").document(bookingCategory.id)

        categoryRef.set(bookingCategory)
            .addOnSuccessListener {
                retrieveCategories()
            }
            .addOnFailureListener { e ->
                Log.e("AdminBookingViewModel", "Failed to add category: ${e.message}")
            }
    }

    fun addBookingItem(bookingItem: BookingItem, selectedCategory: String) {
        val companyName = getCompanyName() ?: return

        if (selectedCategory.isEmpty()) {
            Log.e("AdminBookingViewModel", "Selected category is empty")
            return
        }

        Log.d("AdminBookingViewModel", "Adding item to category: $selectedCategory")
        Log.d("AdminBookingViewModel", "BookingItem ID: ${bookingItem.id}")
        Log.d("AdminBookingViewModel", "BookingItem Name: ${bookingItem.name}")

        try {
            // Use the same path structure as categories
            val documentRef = db.collection("companies").document(companyName)
                .collection("bookings").document("categories")
                .collection("items").document(selectedCategory)
                .collection("items").document(bookingItem.id)

            documentRef.set(bookingItem)
                .addOnSuccessListener {
                    Log.d("AdminBookingViewModel", "Item added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("AdminBookingViewModel", "Failed to add item: ${e.message}")
                }
        } catch (e: IllegalArgumentException) {
            Log.e("AdminBookingViewModel", "Invalid document reference: ${e.message}")
        } catch (e: Exception) {
            Log.e("AdminBookingViewModel", "Error adding booking item", e)
        }
    }

    fun addBookingExtra(bookingItem: BookingItem, bookingExtra: BookingExtra, selectedCategory: String) {
        val companyName = getCompanyName() ?: return

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
            val documentRef = db.collection("companies").document(companyName)
                .collection("bookings").document("categories")
                .collection("items").document(selectedCategory)
                .collection("items").document(bookingItem.id)
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
        val companyName = getCompanyName() ?: return

        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("items")
            .document(selectedCategory)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val itemList = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val quantity = doc.getString("quantity") ?: ""
                    val pricePerDay = doc.getString("pricePerDay") ?: ""
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
                _bookingItems.value = itemList

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
            .collection("items")
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