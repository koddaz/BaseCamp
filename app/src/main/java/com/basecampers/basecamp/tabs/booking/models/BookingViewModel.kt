package com.basecampers.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookingViewModel : ViewModel() {

    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)

    private val _formattedDateRange = MutableStateFlow<String>("")
    val formattedDateRange: StateFlow<String> = _formattedDateRange

    private val _selectedBookingItem = MutableStateFlow<BookingItems?>(null)
    val selectedBookingItem: StateFlow<BookingItems?> = _selectedBookingItem

    private val _selectedExtraItems = MutableStateFlow<List<ExtraItems>>(emptyList())
    val seleectedExtraItems: StateFlow<List<ExtraItems>> = _selectedExtraItems

    val currentUserId = Firebase.auth.currentUser?.uid

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
            "totalPrice" to (selectedItem.price + seleectedExtraItems.value.sumOf { it.price })
        )

        Firebase.firestore.collection("users")
            .document(currentUserUid)
            .collection("bookings")
            .add(booking)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

}