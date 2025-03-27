package com.basecampers.basecamp.tabs.booking.models

class BookingItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,
    val quantity: Int
)

object BookingCatalog {
    val items = listOf(
        BookingItems(
            id = 1,
            name = "Shared Dorm Room",
            info = "One bed in a shared room",
            price = 25.0,
            quantity = 1
        ),
        BookingItems(
            id = 2,
            name = "Single Room",
            info = "A room all to yourself",
            price = 25.0,
            quantity = 1
        ),
        BookingItems(
            id = 3,
            name = "Camping Spot",
            info = "A spot to pitch your tent",
            price = 25.0,
            quantity = 1
        ),

        )
}