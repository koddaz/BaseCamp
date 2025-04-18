package com.basecampers.basecamp.tabs.booking.admin.bookingOverview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import java.text.SimpleDateFormat
import java.util.Locale

class BookingListViewModel: ViewModel() {

}

@Composable
fun AdminCurrentBookings(bookingViewModel: AdminBookingViewModel = viewModel()) {

}

@Composable
fun BookingCard(booking: UserBookingModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = booking.bookingItem?.name ?: "Unknown item",
                style = typography.titleMedium
            )
            Text(
                text = "Total: $${booking.totalPrice}",
                style = typography.bodyMedium
            )
            Text(
                text = "Booked on: ${booking.timestamp?.let {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                } ?: "Unknown date"}",
                style = typography.bodySmall
            )
            Text(
                text = "User ID: ${booking.userId}",
                style = typography.bodySmall
            )
        }
    }
}