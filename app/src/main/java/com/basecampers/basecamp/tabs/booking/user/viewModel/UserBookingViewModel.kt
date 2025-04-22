package com.basecampers.basecamp.tabs.booking.user.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.format

class UserBookingViewModel : ViewModel() {

    // User information
    val currentCompanyId = UserSession.companyProfile.value?.companyId
    val userId = UserSession.userId.value
    private val db = Firebase.firestore

    // Current bookings
    private val _currentBookings = MutableStateFlow<List<UserBookingModel>>(emptyList())
    val currentBookings: StateFlow<List<UserBookingModel>> = _currentBookings

    // Selected for editing
    private val _selectedBooking = MutableStateFlow<UserBookingModel?>(null)
    val selectedBooking = _selectedBooking.asStateFlow()

    // Selected items for everythung
    private val _selectedExtraItems = MutableStateFlow<List<BookingExtra>>(emptyList())
    private val _selectedCategory = MutableStateFlow<BookingCategories?>(null)
    private val _selectedBookingItem = MutableStateFlow<BookingItem?>(null)

    val selectedCategory: StateFlow<BookingCategories?> = _selectedCategory
    val selectedExtraItems: StateFlow<List<BookingExtra>> = _selectedExtraItems
    val selectedBookingItem: StateFlow<BookingItem?> = _selectedBookingItem

    // Lists for retrieving data
    private val _bookingItemsList = MutableStateFlow<List<BookingItem>>(emptyList())
    private val _bookingExtraList = MutableStateFlow<List<BookingExtra>>(emptyList())
    private val _categoriesList = MutableStateFlow<List<BookingCategories>>(emptyList())

    val bookingItemsList: StateFlow<List<BookingItem>> = _bookingItemsList
    val bookingExtraList: StateFlow<List<BookingExtra>> = _bookingExtraList
    val categoriesList: StateFlow<List<BookingCategories>> = _categoriesList

    // For calculation
    private val _selectedDateRange = MutableStateFlow<Pair<Long?, Long?>>(Pair(null, null))
    private val _startDate = MutableStateFlow<Long?>(null)
    private val _endDate = MutableStateFlow<Long?>(null)
    private val _amountOfDays = MutableStateFlow<Int>(0)
    private val _formattedDateRange = MutableStateFlow<String>("")
    private val _finalPrice = MutableStateFlow<Double>(0.0)

    val startDate: StateFlow<Long?> = _startDate
    val endDate: StateFlow<Long?> = _endDate
    val amountOfDays: StateFlow<Int> = _amountOfDays
    val formattedDateRange: StateFlow<String> = _formattedDateRange
    val finalPrice: StateFlow<Double> = _finalPrice


    init {
        retrieveCurrentBookings()
    }

    fun setSelectedBookingItem(item: BookingItem?) {
        _selectedBookingItem.value = item
    }

    fun setSelectedCategory(category: BookingCategories) {
        _selectedCategory.value = category
    }

    fun setSelectedBooking(booking: UserBookingModel){
        _selectedBooking.value = booking
    }

    fun clearAllValues() {
        _selectedDateRange.value = Pair(null, null)
        _startDate.value = null
        _endDate.value = null
        _formattedDateRange.value = ""
        _selectedBookingItem.value = null
        _selectedExtraItems.value = emptyList()
        _amountOfDays.value = 0
        _finalPrice.value = 0.0
    }

    fun updatePriceCalculation() {
        val extraPrice = selectedExtraItems.value.sumOf { it.price.toDoubleOrNull() ?: 0.0 }

        val pricePerDay = selectedBookingItem.value?.pricePerDay?.toDoubleOrNull() ?: 0.0

        val daysCount = amountOfDays.value
        val totalPrice = (pricePerDay * daysCount) + extraPrice

        _finalPrice.value = totalPrice
    }

    fun updateDateRange(start: Long?, end: Long?) {
        _selectedDateRange.value = Pair(start, end)
        _startDate.value = start
        _endDate.value = end

        val startFormatted = start?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "No start date"

        val endFormatted = end?.let {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
        } ?: "No end date"

        _formattedDateRange.value = "$startFormatted - $endFormatted"

        if (start != null && end != null) {
            val diff = end - start
            val days = diff / (1000 * 60 * 60 * 24)
            _amountOfDays.value = days.toInt()
        } else {
            _amountOfDays.value = 0
        }
    }

    fun retrieveCategories() {
        if (currentCompanyId == null) {
            Log.d("UserBookingViewModel", "Company ID is empty")
            return
        }

        db.collection("companies").document(currentCompanyId)
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

    fun saveBooking() {
        val bookingId = db.collection("bookings").document().id

        val userBookingModel = UserBookingModel(
            userId = userId.toString(),
            bookingId = bookingId,
            bookingItem = selectedBookingItem.value?.name ?: "",
            extraItems = selectedExtraItems.value,
            startDate = startDate.value,
            endDate = endDate.value,
            totalPrice = finalPrice.value,
            status = BookingStatus.PENDING
        )

        try {
            val userRef = db
                .collection("companies").document(currentCompanyId.toString())
                .collection("users").document(userId.toString())
                .collection("bookings").document(bookingId)  // Use specific bookingId

            val companyRef = db
                .collection("companies").document(currentCompanyId.toString())
                .collection("bookings").document(bookingId)  // Use same bookingId

            val batch = db.batch()
            batch.set(userRef, userBookingModel)
            batch.set(companyRef, userBookingModel)

            batch.commit()
                .addOnSuccessListener {
                    Log.d("UserBookingViewModel", "Booking saved successfully")
                    clearAllValues()
                    retrieveCurrentBookings()
                }
                .addOnFailureListener { e ->
                    Log.e("UserBookingViewModel", "Error saving booking", e)
                }
        } catch (e: Exception) {
            Log.e("UserBookingViewModel", "Error saving booking", e)
        }
    }

    fun retrieveBookingItems(categoryId: String) {
        db
            .collection("companies").document(currentCompanyId.toString())
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



    fun retrieveExtraItems(
        categoryId: String,
        itemId: String,
    ) {
        db.collection("companies").document(currentCompanyId.toString())
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

    fun removeExtraItem(itemId: String) {
        _selectedExtraItems.value = _selectedExtraItems.value.filter { it.id != itemId }
    }






    private fun retrieveExtraList(document: DocumentSnapshot): List<BookingExtra> {
        return try {
            val extrasList = document.get("extraItems") as? List<*>
            extrasList?.mapNotNull { item ->
                when (item) {
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        val map = item as Map<String, Any>
                        BookingExtra(
                            id = map["id"] as? String ?: "",
                            name = map["name"] as? String ?: "",
                            info = map["info"] as? String ?: "",
                            price = map["price"] as? String ?: "0.0"
                        )
                    }
                    else -> null
                }
            } ?: emptyList()
        } catch (e: Exception) {
            Log.e("ManageBookingsViewModel", "Error parsing extraItems", e)
            emptyList()
        }
    }

    fun retrieveCurrentBookings() {
        Log.d("ManageBookingsViewModel", "Retrieving bookings for company: ${currentCompanyId}")

        if (currentCompanyId.isNullOrEmpty()) {
            Log.e("ManageBookingsViewModel", "Company ID is empty, skipping retrieval")
            return
        }

        db.collection("companies").document(currentCompanyId.toString())
            .collection("users").document(userId.toString())
            .collection("bookings").get()
            .addOnSuccessListener { snapshot ->
                val bookingsList = snapshot.documents.mapNotNull { document ->
                    try {
                        UserBookingModel(
                            userId = document.getString("userId") ?: document.id,
                            bookingId = document.getString("bookingId") ?: document.id,
                            bookingItem = document.getString("bookingItem") ?: "",
                            extraItems = retrieveExtraList(document),
                            startDate = document.getLong("startDate"),
                            endDate = document.getLong("endDate"),
                            timestamp = document.getDate("timestamp") ?: Date(),
                            totalPrice = document.getDouble("totalPrice") ?: 0.0,
                            status = try {
                                val statusStr = document.getString("status")
                                if (statusStr != null) BookingStatus.valueOf(statusStr) else BookingStatus.PENDING
                            } catch (e: Exception) {
                                Log.e("ManageBookingsViewModel", "Error parsing status", e)
                                BookingStatus.PENDING
                            }
                        )
                    } catch (e: Exception) {
                        Log.e("ManageBookingsViewModel", "Error parsing booking document", e)
                        null

                    }
                }
                _currentBookings.value = bookingsList
            }
            .addOnFailureListener { exception ->
                Log.e("ManageBookingsViewModel", "Error retrieving bookings", exception)
                _currentBookings.value = emptyList()
            }
    }
    }

