package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.components.CurrentBookingCard
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserCurrentBookings(
    bookingViewModel: UserBookingViewModel,
    goBack: () -> Unit
) {
    val currentBookings by bookingViewModel.currentBookings.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Remove unnecessary nested Column
        currentBookings.forEach { booking ->
            CurrentBookingCard(
                bookingViewModel = bookingViewModel,
                booking = booking,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        CustomButton(
            text = "Back",
            onClick = goBack  // Fixed: remove the curly braces
        )
    }
}