package com.basecampers.basecamp.tabs.booking.models

data class BookingCategories(
    val id: String,
    val name: String,
    val info: String,
    val createdBy: String = "",
    )

data class BookingItem(
    val id: String,
    val categoryId: String,
    val pricePerDay: String,
    val name: String,
    val info: String,
    val quantity: String,
    val createdBy: String = "",
    )

data class BookingExtra(
    val id: String,
    val price: String,
    val name: String,
    val info: String,
)