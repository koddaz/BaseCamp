package com.basecampers.basecamp.tabs.booking.models

import java.util.Date


data class UserBookingModel(

    val userId: String,
    val bookingItem: BookingItem?,
    val extraItems: List<BookingExtra>,
    val startDate: Long?,
    val endDate: Long?,
    val timestamp: Date = Date(),
    val totalPrice: Double,

)