package com.basecampers.basecamp.tabs.booking.admin.bookingOverview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import com.basecampers.basecamp.tabs.booking.admin.viewModel.ManageBookingsViewModel
import com.basecampers.basecamp.tabs.booking.components.CurrentBookingCard


@Composable
fun AdminCurrentBookings(
    manageViewModel: ManageBookingsViewModel = viewModel(),
) {
    var scrollState = rememberScrollState()


    val openBookings by manageViewModel.openBookings.collectAsState()



    Column(Modifier.fillMaxSize().verticalScroll(scrollState)) {

        val bookingsByStatus = openBookings.groupBy { it.status }

        bookingsByStatus[BookingStatus.PENDING]?.let { pendingBookings ->
            Text(
                text = "PENDING BOOKINGS (${pendingBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            pendingBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, manageViewModel = manageViewModel)
            }
        }

        bookingsByStatus[BookingStatus.CONFIRMED]?.let { confirmedBookings ->
            Text(
                text = "CONFIRMED BOOKINGS (${confirmedBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            confirmedBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, manageViewModel = manageViewModel)
            }
        }

        bookingsByStatus[BookingStatus.CANCELED]?.let { canceledBookings ->
            Text(
                text = "CANCELED BOOKINGS (${canceledBookings.size})",
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp, 8.dp)
            )
            canceledBookings.forEach { booking ->
                CurrentBookingCard(booking = booking, manageViewModel = manageViewModel)
            }
        }
    }



}

