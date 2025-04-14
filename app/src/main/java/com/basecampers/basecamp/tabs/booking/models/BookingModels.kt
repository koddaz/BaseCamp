package com.basecampers.basecamp.tabs.booking.models

import java.util.Date

data class BookingItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,
    val categoryId: String // Add this field
)


data class UserBooking(
    val id: String,
    val bookingItem: BookingItems?,
    val extraItems: List<BookingExtra>,
    val timestamp: Date?,
    val timeRange: String,
    val totalPrice: Double,
    val userId: String
)