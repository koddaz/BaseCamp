package com.example.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
import com.example.basecamp.UserModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.text.get

class AdminBookingViewModel : ViewModel() {
    val db = Firebase.firestore
    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories : StateFlow<List<BookingCategories>> = _categories




    fun addBookingItem(user: UserModel, bookingItem: BookingItem, selectedCategory: String) {
        db.collection("companies")
            .document(user.companyName)
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(selectedCategory)
            .collection("items")
            .add(bookingItem)
            .addOnSuccessListener {
                retrieveBookingItems(
                    user = user,
                    selectedCategory = selectedCategory
                )
            }
    }

    fun addBookingCategory(user: UserModel, bookingCategory: BookingCategories) {
        db.collection("companies")
            .document(user.companyName)
            .collection("bookings")
            .document("categories")
            .collection("categories")
            .add(bookingCategory)
            .addOnSuccessListener {
                retrieveCategories(user)
            }
    }

    fun retrieveBookingItems(user: UserModel, selectedCategory: String) {
        db.collection("companies")
            .document(user.companyName)
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
            .document(selectedCategory)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                // Handle success
            }
    }

    fun retrieveCategories(user: UserModel) {
        db.collection("companies")
            .document(user.companyName)
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



}