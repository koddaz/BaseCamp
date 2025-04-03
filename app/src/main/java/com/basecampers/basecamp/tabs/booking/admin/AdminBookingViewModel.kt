package com.basecampers.basecamp.tabs.booking.admin

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore
    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories : StateFlow<List<BookingCategories>> = _categories


    init {
        retrieveCategories()
    }

    fun addBookingItem(bookingItem: BookingItem, selectedCategory: String) {
        db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(selectedCategory)
            .collection("items")
            .add(bookingItem)
            .addOnSuccessListener {

            }
    }

    fun addBookingCategory(bookingCategory: BookingCategories) {
        db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .add(bookingCategory)
            .addOnSuccessListener {
                retrieveCategories()
            }
    }

    fun retrieveBookingItems(selectedCategory: String) {
        db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(selectedCategory)
    }

    fun retrieveCategories() {
        db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("bookingCategories")
            .collection("category").get()
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

}