package com.basecampers.basecamp.tabs.booking.admin.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED
}

class ManageBookingsViewModel() : ViewModel() {
    private val db = Firebase.firestore
    val companyProfile = UserSession.companyProfile.value

    private val _openBookings = MutableStateFlow<List<UserBookingModel>>(emptyList())
    val openBookings = _openBookings.asStateFlow()

    init {
        retrieveBookings(companyProfile?.companyId ?: "")
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
                            bookingId = document.getString("bookingId") ?: document.id,
                            bookingItem = (document.get("bookingItem")) as String,
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
                _openBookings.value = bookingsList
            }
            .addOnFailureListener { exception ->
                Log.e("ManageBookingsViewModel", "Error retrieving bookings", exception)
                _openBookings.value = emptyList()
            }
    }

    fun cancelStatus(booking: UserBookingModel) {
        Log.d("ManageBookingsViewModel", "Cancelling status for booking: ${booking.userId}")
        val companyId = companyProfile?.companyId ?: ""
        if (companyId.isEmpty()) {
            Log.e("ManageBookingsViewModel", "Company ID is empty, can't update booking")
            return
        }
        val updatedBooking = booking.copy(status = BookingStatus.CANCELED)

        val companyRef = db.collection("companies").document(companyId)
            .collection("bookings").document(booking.bookingId)
        val userRef = db.collection("companies").document(companyId)
            .collection("users").document(booking.userId)
            .collection("bookings").document(booking.bookingId)
        companyRef.get().addOnSuccessListener { companyDoc ->
            if (companyDoc.exists()) {
                // Convert enum to string when updating
                companyRef.update("status", BookingStatus.CANCELED.toString())
                    .addOnSuccessListener {
                        Log.d("ManageVM", "Company booking status updated")

                        // Now check and update user document
                        userRef.get().addOnSuccessListener { userDoc ->
                            if (userDoc.exists()) {
                                userRef.update("status", BookingStatus.CANCELED.toString())
                                    .addOnSuccessListener {
                                        // Update local state
                                        val currentList = _openBookings.value
                                        val updatedList = currentList.map {
                                            if (it.bookingId == booking.bookingId) updatedBooking else it
                                        }
                                        _openBookings.value = updatedList
                                        Log.d("ManageVM", "Both documents updated successfully")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ManageVM", "Failed to update user doc", e)
                                    }
                            } else {
                                Log.e("ManageVM", "User booking document doesn't exist")
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ManageVM", "Failed to update company doc", e)
                    }
            } else {
                Log.e("ManageVM", "Company booking document doesn't exist")
            }
        }
    }

    fun confirmStatus(booking: UserBookingModel) {
        val companyId = companyProfile?.companyId ?: ""
        if (companyId.isEmpty()) return

        val updatedBooking = booking.copy(status = BookingStatus.CONFIRMED)
        val companyRef = db.collection("companies").document(companyId)
            .collection("bookings").document(booking.bookingId)
        val userRef = db.collection("companies").document(companyId)
            .collection("users").document(booking.userId)
            .collection("bookings").document(booking.bookingId)

        // Check if company document exists, then update
        companyRef.get().addOnSuccessListener { companyDoc ->
            if (companyDoc.exists()) {
                // Convert enum to string when updating
                companyRef.update("status", BookingStatus.CONFIRMED.toString())
                    .addOnSuccessListener {
                        Log.d("ManageVM", "Company booking status updated")

                        // Now check and update user document
                        userRef.get().addOnSuccessListener { userDoc ->
                            if (userDoc.exists()) {
                                userRef.update("status", BookingStatus.CONFIRMED.toString())
                                    .addOnSuccessListener {
                                        // Update local state
                                        val currentList = _openBookings.value
                                        val updatedList = currentList.map {
                                            if (it.bookingId == booking.bookingId) updatedBooking else it
                                        }
                                        _openBookings.value = updatedList
                                        Log.d("ManageVM", "Both documents updated successfully")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ManageVM", "Failed to update user doc", e)
                                    }
                            } else {
                                Log.e("ManageVM", "User booking document doesn't exist")
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ManageVM", "Failed to update company doc", e)
                    }
            } else {
                Log.e("ManageVM", "Company booking document doesn't exist")
            }
        }
    }
}