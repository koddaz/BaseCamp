package com.example.basecamp.tabs.booking.models

class BookingCategories(
    val id: String,
    val name: String,
    val info: String,
    val createdBy: String = "",
    )

class BookingItem(
    val id: String,
    val pricePerDay: Double,
    val name: String,
    val info: String,
    val quantity: Int = 1,
    val createdBy: String = "",
    )

class BookingExtra(
    val id: String,
    val price: Double,
    val name: String,
    val info: String,
)