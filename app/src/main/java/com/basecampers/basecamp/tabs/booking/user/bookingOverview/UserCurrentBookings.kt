package com.basecampers.basecamp.tabs.booking.user.bookingOverview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import com.basecampers.basecamp.tabs.booking.components.CurrentBookingCard
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import kotlin.collections.forEach

@Composable
fun UserCurrentBookings(
    bookingViewModel: UserBookingViewModel,
    goBack: () -> Unit
) {
    val currentBookings by bookingViewModel.currentBookings.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        val bookingsByStatus = currentBookings.groupBy { it.status }

        bookingsByStatus[BookingStatus.PENDING]?.let { pendingBookings ->
            Text(
                text = "PENDING BOOKINGS (${pendingBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            pendingBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, bookingViewModel = bookingViewModel, edit = true)
            }
        }

        bookingsByStatus[BookingStatus.CONFIRMED]?.let { pendingBookings ->
            Text(
                text = "CONFIRMED BOOKINGS (${pendingBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            pendingBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, bookingViewModel = bookingViewModel)
            }
        }

        bookingsByStatus[BookingStatus.CANCELED]?.let { pendingBookings ->
            Text(
                text = "CANCELED BOOKINGS (${pendingBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            pendingBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, bookingViewModel = bookingViewModel)
            }
        }


        Spacer(modifier = Modifier.weight(1f))
        CustomButton(
            text = "Back",
            onClick = goBack  // Fixed: remove the curly braces
        )
    }
}