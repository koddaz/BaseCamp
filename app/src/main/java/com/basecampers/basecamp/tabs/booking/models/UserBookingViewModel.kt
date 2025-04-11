package com.basecampers.basecamp.tabs.booking.models

import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UserBookingViewModel : ViewModel() {

    val db = Firebase.firestore
    private val _selectedItemId = MutableStateFlow("")
    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)

    val selectedItemId : StateFlow<String> = _selectedItemId.asStateFlow()

    private val _bookingItemsList = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItemsList: StateFlow<List<BookingItem>> = _bookingItemsList

    private val _bookingExtraList = MutableStateFlow<List<BookingExtra>>(emptyList())
    val bookingExtraList: StateFlow<List<BookingExtra>> = _bookingExtraList

    private val _categories = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categories: StateFlow<List<BookingCategories>> = _categories

    private val _formattedDateRange = MutableStateFlow<String>("")
    val formattedDateRange: StateFlow<String> = _formattedDateRange

    private val _selectedBookingItem = MutableStateFlow<BookingItem?>(null)
    val selectedBookingItem: StateFlow<BookingItem?> = _selectedBookingItem

    private val _selectedExtraItems = MutableStateFlow<List<BookingExtra>>(emptyList())
    val seleectedExtraItems: StateFlow<List<BookingExtra>> = _selectedExtraItems

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
        categoryId: String,
        itemId: String,
    ) {
        val companyName = getCompanyName() ?: return
        db.collection("companies").document(companyName)
            .collection("bookings").document("categories")
            .collection("items").document(categoryId)
            .collection("items").document(itemId)
            .collection("extras").get().addOnSuccessListener { snapshot ->
                val extras = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val info = doc.getString("info") ?: ""
                    val price = doc.getString("price") ?: "0.0"
                    BookingExtra(id, name, info, price)
                }
                val currentExtrasMap =  _bookingExtraList.value.associateBy { it.id }.toMutableMap()

                extras.forEach { item ->
                    currentExtrasMap[item.id] = item
                }

                _bookingExtraList.value = currentExtrasMap.values.toList()

            }

    }



    fun addExtraItem(item: BookingExtra) {
        _selectedExtraItems.value = _selectedExtraItems.value + item
    }

    fun removeExtraItem(item: BookingExtra) {
        _selectedExtraItems.value = _selectedExtraItems.value - item
    }

    fun setSelection(
        itemId: String,
        item: BookingItem? = null,
        ) {
        _selectedItemId.value = itemId
        if (item != null) {
            _selectedBookingItem.value = item

        }
    }

    fun calculateDays(startDate: Long?, endDate: Long?) {
        if (startDate != null && endDate != null) {
            val diff = endDate - startDate
            val days = diff / (1000 * 60 * 60 * 24)
            _amountOfDays.value = days.toInt()
        }
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

        calculateDays(startDate, endDate)
        _formattedDateRange.value = "$startFormatted - $endFormatted"
    }

    fun setSelectedBookingItem(item: BookingItem?) {
        _selectedBookingItem.value = item

    }

    fun removeSelectedBookingItem() {
        _selectedBookingItem.value = null
    }

}