package com.example.basecamp.tabs.booking.models

import java.util.Date

data class BookingItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,

)

data class ExtraItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,
)

data class UserBooking(
    val id: String,
    val bookingItem: BookingItems?,
    val extraItems: List<ExtraItems>,
    val timestamp: Date?,
    val totalPrice: Double,
    val userId: String
)