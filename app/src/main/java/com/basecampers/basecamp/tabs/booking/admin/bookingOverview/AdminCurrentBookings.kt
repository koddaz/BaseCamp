package com.basecampers.basecamp.tabs.booking.admin.bookingOverview

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.admin.viewModel.ManageBookingsViewModel
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.google.firebase.Firebase
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun AdminCurrentBookings(
    manageViewModel: ManageBookingsViewModel = viewModel(),
    companyViewModel: CompanyViewModel = viewModel(),
    bookingViewModel: AdminBookingViewModel = viewModel(),
    goBack: () -> Unit
) {


    val openBookings by manageViewModel.openBookings.collectAsState()



    Column(Modifier.fillMaxSize()) {
        openBookings.forEach { booking ->
            CustomColumn {
                Text(text = "Booking ID: ${booking.userId}")
                Text(text = "Booking Item: ${booking.bookingItem?.name ?: "Unknown"}")
                Text(text = "Total Price: $${booking.totalPrice}")
                Text(
                    text = "Booking Date: ${
                        booking.timestamp?.let {
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                        } ?: "Unknown date"
                    }")
            }

        }
    }



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