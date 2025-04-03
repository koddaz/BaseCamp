package com.example.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
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

    init {
        retrieveCategories()
    }

    fun retrieveCategories() {
        db.collection("companies")
            .document("companyName")
            .collection("bookings")
            .document("bookingCategories")
            .collection("category")
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
        db.collection("companies")
            .document("companyName")
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