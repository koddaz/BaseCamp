package com.basecampers.basecamp.tabs.booking.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class UserBookingViewModel : ViewModel() {
    val userId = Firebase.auth.currentUser?.uid
    val db = Firebase.firestore
    val authViewModel = AuthViewModel()


    private val _selectedItemId = MutableStateFlow("")
    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)


    val _selectedCategory = MutableStateFlow<BookingCategories?>(null)
    val selectedCategory: StateFlow<BookingCategories?> = _selectedCategory

    val _selectedExtraItems = MutableStateFlow<List<BookingExtra>>(emptyList())
    val selectedExtraItems: StateFlow<List<BookingExtra>> = _selectedExtraItems

    private val _selectedBookingItem = MutableStateFlow<BookingItem?>(null)
    val selectedBookingItem: StateFlow<BookingItem?> = _selectedBookingItem

    private val _bookingItemsList = MutableStateFlow<List<BookingItem>>(emptyList())
    val bookingItemsList: StateFlow<List<BookingItem>> = _bookingItemsList

    private val _bookingExtraList = MutableStateFlow<List<BookingExtra>>(emptyList())
    val bookingExtraList: StateFlow<List<BookingExtra>> = _bookingExtraList

    private val _categoriesList = MutableStateFlow<List<BookingCategories>>(emptyList())
    val categoriesList: StateFlow<List<BookingCategories>> = _categoriesList


    private val _amountOfDays = MutableStateFlow<Int>(0)
    private val _formattedDateRange = MutableStateFlow<String>("")
    val _finalPrice = MutableStateFlow<Double>(0.0)

    val startDate: StateFlow<Long?> = _startDate
    val endDate: StateFlow<Long?> = _endDate
    val amountOfDays: StateFlow<Int> = _amountOfDays
    val formattedDateRange: StateFlow<String> = _formattedDateRange
    val finalPrice: StateFlow<Double> = _finalPrice

    private val _user = MutableStateFlow<CompanyProfileModel?>(null)
    val user: StateFlow<CompanyProfileModel?> = _user


    fun setUser(companyProfileModel: CompanyProfileModel) {
        _user.value = companyProfileModel
        retrieveCategories()
    }


    private fun getCompanyId(): String? {
        return _user.value?.companyId
        Log.d("UserBookingViewModel", "Company ID: ${_user.value?.companyId}")
    }

    fun setSelectedCategory(category: BookingCategories) {
        _selectedCategory.value = category
    }

    fun retrieveCategories() {
       val companyId = getCompanyId().toString()
        if (userId != null) {
            if (companyId.isEmpty()) {
                Log.d("UserBookingViewModel", "Company ID is empty")
                return
            }
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
                    _categoriesList.value = categoryList
                }
                .addOnFailureListener { e ->
                    // Log the error
                }
        }
    }
    fun clearAllValues() {
        _selectedItemId.value = ""
        _selectedDateRange.value = Pair(null, null)
        _startDate.value = null
        _endDate.value = null
        _formattedDateRange.value = ""
        _selectedBookingItem.value = null
        _selectedExtraItems.value = emptyList()
        _amountOfDays.value = 0
        _finalPrice.value = 0.0
    }


    fun saveUserBooking(userBooking: UserBooking) {
        val companyId = getCompanyId() ?: return
        val userId = authViewModel.getCurrentUserUid() ?: return

        try {
            // Create a flattened data structure with only essential fields
            val bookingData = hashMapOf(
                "userId" to userBooking.userId,
                "companyId" to userBooking.companyId,
                "bookingItemId" to userBooking.bookingItem?.id,
                "bookingItemName" to userBooking.bookingItem?.name,
                "startDate" to userBooking.startDate,
                "endDate" to userBooking.endDate,
                "totalPrice" to userBooking.totalPrice,
                "createdAt" to userBooking.createdAt,
                "extraItems" to userBooking.extraItems.map { extra ->
                    mapOf(
                        "id" to extra.id,
                        "name" to extra.name,
                        "price" to extra.price
                    )
                }
            )

            val userRef = db
                .collection("companies").document(companyId)
                .collection("users").document(userId)
                .collection("bookings").document()

            val companyRef = db
                .collection("companies").document(companyId)
                .collection("bookings").document()

            // Use batch write to ensure both writes succeed or fail together
            val batch = db.batch()
            batch.set(userRef, bookingData)
            batch.set(companyRef, bookingData)

            batch.commit()
                .addOnSuccessListener {
                    Log.d("UserBookingViewModel", "Booking saved successfully")
                    clearAllValues()
                }
                .addOnFailureListener { e ->
                    Log.e("UserBookingViewModel", "Error saving booking", e)
                }

        } catch (e: Exception) {
            Log.e("UserBookingViewModel", "Error saving booking", e)
        }
    }




    fun retrieveBookingItems(categoryId: String) {
        val companyId = getCompanyId() ?: return

        db
            .collection("companies").document(companyId)
            .collection("categories").document(categoryId)
            .collection("bookings")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents.mapNotNull { doc ->

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

    fun calculateExtra(extras: List<BookingExtra>): Double {
        return extras.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
    }

    fun calculateTotalPrice(item: BookingItem, extras: List<BookingExtra>) {
        val pricePerDay = item.pricePerDay.toDoubleOrNull() ?: 0.0
        val extraPrice = extras.sumOf {it.price.toDoubleOrNull() ?: 0.0}
        val totalPrice = (pricePerDay * amountOfDays.value) + extraPrice

        _finalPrice.value = totalPrice
    }

    fun retrieveExtraItems(
        categoryId: String,
        itemId: String,
    ) {
        val companyId = getCompanyId() ?: return
        db.collection("companies").document(companyId)
            .collection("categories").document(categoryId)
            .collection("bookings").document(itemId)
            .collection("extras").get().addOnSuccessListener { snapshot ->
                val extras = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: ""
                    val price = doc.getString("price") ?: "0.0"
                    val info = doc.getString("info") ?: ""

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
    fun clearSelectedExtras() {
        _selectedExtraItems.value = emptyList()
    }
    fun removeSelectedBookingItem() {
        _selectedBookingItem.value = null
    }

}