package com.basecampers.basecamp.tabs.booking.user.createBooking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.components.CustomButton

@Composable
fun UserBookingMainView(
    navToBooking: () -> Unit,
    navToCurrentBookings: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomButton(
            text = "Book an item",
            onClick = {
                navToBooking()
            }
        )
        CustomButton(
            text = "See current bookings",
            onClick = {
                navToCurrentBookings()
            }
        )
    }
}