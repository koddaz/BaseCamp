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
    val id: String = "",
    val userId: String,
    val companyId: String,
    val bookingItem: BookingItem?,
    val extraItems: List<BookingExtra>,
    val startDate: Long?,
    val endDate: Long?,
    val timestamp: Date = Date(),
    val totalPrice: Double,
    val createdAt: Long = System.currentTimeMillis()
)