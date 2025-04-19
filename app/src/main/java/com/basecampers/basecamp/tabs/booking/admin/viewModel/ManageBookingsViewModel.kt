package com.basecampers.basecamp.tabs.booking.admin.viewModel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.text.get

@Suppress("UNCHECKED_CAST")
class ManageBookingsViewModel() : ViewModel() {
    private val db = Firebase.firestore

    private val _openBookings = MutableStateFlow<List<UserBookingModel>>(emptyList())
    val openBookings = _openBookings.asStateFlow()
    val companyProfile = UserSession.companyProfile.value

    init {
        retrieveBookings(companyProfile?.companyId ?: "")
    }

    fun retrieveBookings(companyId: String) {
        Log.d("ManageBookingsViewModel", "Retrieving bookings for company: ${companyId}")

        // Safeguard against empty ID
        if (companyId.isEmpty()) {
            Log.e("ManageBookingsViewModel", "Company ID is empty, skipping retrieval")
            return
        }

        db.collection("companies").document(companyId)
            .collection("bookings").get()
            .addOnSuccessListener { snapshot ->
                val bookingsList = snapshot.documents.mapNotNull { document ->
                    try {
                        UserBookingModel(
                            userId = document.getString("userId") ?: document.id,
                            bookingItem = document.get("bookingItem") as? BookingItem,
                            extraItems = (document.get("extraItems") as? List<BookingExtra>)
                                ?: emptyList(),
                            startDate = document.getLong("startDate"),
                            endDate = document.getLong("endDate"),
                            timestamp = document.getDate("timestamp") ?: Date(),
                            totalPrice = document.getDouble("totalPrice") ?: 0.0
                        )
                    } catch (e: Exception) {
                        Log.e("ManageBookingsViewModel", "Error parsing booking document", e)
                        null

                    }
                }
                _openBookings.value = bookingsList
            }
            .addOnFailureListener { exception ->
                Log.e("ManageBookingsViewModel", "Error retrieving bookings", exception)
                _openBookings.value = emptyList()
            }
    }
}
