package com.example.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
import com.example.basecamp.tabs.booking.models.UserBooking
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AdminCurrentBookings(adminModel : AdminBookingViewModel = viewModel()) {

}

@Composable
fun BookingCard(booking: UserBooking) {
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