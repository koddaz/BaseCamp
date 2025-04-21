package com.basecampers.basecamp.tabs.booking.models

import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import java.util.Date


data class UserBookingModel(

    val bookingId: String = "",
    val userId: String,
    val bookingItem: String = "",
    val extraItems: List<BookingExtra>,
    val startDate: Long?,
    val endDate: Long?,
    val timestamp: Date = Date(),
    val totalPrice: Double,
    val status: BookingStatus = BookingStatus.PENDING

)