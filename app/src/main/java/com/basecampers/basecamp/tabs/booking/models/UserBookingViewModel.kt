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
import kotlin.collections.addAll
import kotlin.text.format
import kotlin.text.get
import kotlin.text.toInt

class UserBookingViewModel : ViewModel() {

    val db = Firebase.firestore

    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)

    private val _bookingItemsList = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItemsList: StateFlow<List<BookingItem>> = _bookingItemsList

    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories

    private val _formattedDateRange = MutableStateFlow<String>("")
    val formattedDateRange: StateFlow<String> = _formattedDateRange

    private val _selectedBookingItem = MutableStateFlow<BookingItem?>(null)
    val selectedBookingItem: StateFlow<BookingItem?> = _selectedBookingItem

    private val _selectedExtraItems = MutableStateFlow<List<ExtraItems>>(emptyList())
    val seleectedExtraItems: StateFlow<List<ExtraItems>> = _selectedExtraItems

    private val _amountOfDays = MutableStateFlow<Int>(0)
    val amountOfDays: StateFlow<Int> = _amountOfDays

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



    fun retrieveCategories() {
        val companyName = getCompanyName() ?: return
        if (currentUserId != null) {
            db.collection("companies")
                .document(companyName)
                .collection("bookings")
                .document("categories")
                .collection("items")
                .get().addOnSuccessListener { snapshot ->
                    val categoryList = snapshot.documents.mapNotNull { doc ->
                        val id = doc.id
                        val name = doc.getString("name") ?: ""
                        val info = doc.getString("info") ?: ""
                        val createdBy = doc.getString("createdBy") ?: ""
                        BookingCategories(id, name, info, createdBy)
                    }

                    _categories.value = categoryList
                    categoryList.forEach { category ->
                        retrieveBookingItems(category.id)
                    }
                }
        }
    }

    fun retrieveBookingItems(categoryId: String) {
        val companyName = getCompanyName() ?: return

        db.collection("companies")
            .document(companyName)
            .collection("bookings")
            .document("categories")
            .collection("items")
            .document(categoryId)
            .collection("items")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->
                // Changed to create BookingItem objects instead of BookingItems
                    val id = doc.id  // Use document ID as string
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val price = doc.getString("pricePerDay") ?: "0.0" // Get as string to match BookingItem
                    val quantity = doc.getString("quantity") ?: "1"

                    BookingItem(id, categoryId, price, name, info, quantity)
                }
                val currentItemsMap = _bookingItemsList.value.associateBy { it.id }.toMutableMap()

                items.forEach { item ->
                    currentItemsMap[item.id] = item
                }

                _bookingItemsList.value = currentItemsMap.values.toList()
            }
    }

    fun retrieveExtraItems(
        selectedCategory: String,
        bookingItem: BookingItem,
        bookingExtra: BookingExtra
    ) {
        val companyName = getCompanyName() ?: return
        db.collection("companies").document(companyName)
            .collection("bookings").document("categories")
            .collection("items").document(selectedCategory)
            .collection("items").document(bookingItem.id)
            .collection("extras").document(bookingExtra.id).get().addOnSuccessListener { snapshot ->


            }

    }



    fun addExtraItem(item: ExtraItems) {
        _selectedExtraItems.value = _selectedExtraItems.value + item
    }

    fun removeExtraItem(item: ExtraItems) {
        _selectedExtraItems.value = _selectedExtraItems.value - item
    }

    fun setSelection(item: BookingItem, startDate: Long?, endDate: Long?) {
        _selectedBookingItem.value = item
        updateDateRange(startDate, endDate)
    }

    fun updateDateRange(startDate: Long?, endDate: Long?) {
        // Update all date states
        _startDate.value = startDate
        _endDate.value = endDate
        _selectedDateRange.value = Pair(startDate, endDate)

        // Format the date display string
        _formattedDateRange.value = formatDateString(startDate, endDate)

        // Calculate number of days
        _amountOfDays.value = if (startDate != null && endDate != null) {
            calculateDaysBetween(startDate, endDate)
        } else {
            0
        }
    }
    private fun formatDateString(startDate: Long?, endDate: Long?): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val startStr = startDate?.let { dateFormat.format(Date(it)) } ?: "No start date"
        val endStr = endDate?.let { dateFormat.format(Date(it)) } ?: "No end date"

        return "$startStr - $endStr"
    }

    private fun calculateDaysBetween(startDate: Long, endDate: Long): Int {
        val millisecondsPerDay = 24 * 60 * 60 * 1000L
        return ((endDate - startDate) / millisecondsPerDay).toInt() + 1
    }

    fun updateSelectedDateRange(start: Long?, end: Long?) {
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

    fun setSelectedBookingItem(item: BookingItem?) {
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
                "price" to selectedItem.pricePerDay,

                ),
            "extraItems" to seleectedExtraItems.value.map { item ->
                mapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "price" to item.price
                )
            },
            "timestamp" to com.google.firebase.Timestamp.now(),
            "totalPrice" to (selectedItem.pricePerDay + seleectedExtraItems.value.sumOf { it.price }),
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