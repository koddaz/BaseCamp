package com.basecampers.basecamp.tabs.booking.models

class BookingItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,
)

class ExtraItems(
    val id: Int,
    val name: String,
    val info: String,
    val price: Double,
)

object ExtraItemsCatalog {
    val extraItems = listOf(
        ExtraItems(
            id = 1,
            name = "Toothbrush",
            info = "A toothbrush for cleaning your teeth",
            price = 5.0
        ),
        ExtraItems(
            id = 2,
            name = "Towel",
            info = "A towel for covering your mouth",
            price = 5.0
        ),
        ExtraItems(
            id = 3,
            name = "Pillow",
            info = "A pillow for your head",
            price = 5.0
        )
    )
}


object BookingCatalog {
    val items = listOf(
        BookingItems(
            id = 1,
            name = "Shared Dorm Room",
            info = "One bed in a shared room",
            price = 25.0,
        ),
        BookingItems(
            id = 2,
            name = "Single Room",
            info = "A room all to yourself",
            price = 25.0,
        ),
        BookingItems(
            id = 3,
            name = "Camping Spot",
            info = "A spot to pitch your tent",
            price = 25.0,
        ),

        )
}