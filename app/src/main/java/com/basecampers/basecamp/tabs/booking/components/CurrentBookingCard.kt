package com.basecampers.basecamp.tabs.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import com.basecampers.basecamp.tabs.booking.admin.viewModel.ManageBookingsViewModel
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CurrentBookingCard(
    booking: UserBookingModel,
    bookingViewModel: UserBookingViewModel? = null,
    manageViewModel: ManageBookingsViewModel? = null,
    navToEditBooking: () -> Unit = {}
) {
    var isVisible by remember { mutableStateOf(false) }
    val isAdmin = manageViewModel != null

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier
            .background(
                when (booking.status) {
                    BookingStatus.CONFIRMED -> Color.Green.copy(alpha = 0.3f)
                    BookingStatus.PENDING -> Color.Yellow.copy(alpha = 0.3f)
                    BookingStatus.CANCELED -> Color.Red.copy(alpha = 0.3f)
                })
            .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth().clickable(
                onClick = {
                    isVisible = !isVisible
                }
            )) {
                Column {
                    Row {
                        Text(
                            text = booking.bookingItem,
                            style = typography.titleMedium
                        )
                        Text(text = " (${booking.status})")
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Drop-down Arrow")
                    }
                    Text(
                        text = "Booked on: ${
                            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(booking.timestamp)
                        }",
                        style = typography.bodySmall
                    )
                }
            }
            if (isVisible) {
                if (booking.extraItems.isNotEmpty()) {
                    Text("Extra items: ")
                    booking.extraItems.forEach { extraItem ->
                        Row {
                            Text(
                                text = extraItem.name,
                                style = typography.bodySmall
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = extraItem.price,
                                style = typography.bodySmall
                            )
                        }
                    }
                }
                Text(
                    text = "Booked by: ${booking.userId}",
                    style = typography.bodySmall
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Total: $${booking.totalPrice}",
                        style = typography.bodyMedium
                    )
                }

                // Only show admin actions when using ManageBookingsViewModel
                if (isAdmin && manageViewModel != null) {
                    Row {
                        CustomButton(text = "Confirm", onClick = {
                            manageViewModel.confirmStatus(booking)
                        })
                        CustomButton(text = "Cancel", onClick = {
                            manageViewModel.cancelStatus(booking)
                        })
                    }
                } else {
                    Row {
                        CustomButton(text = "Edit", onClick = {
                            bookingViewModel?.setSelectedBooking(booking)
                            navToEditBooking()
                        })
                        CustomButton(text = "Cancel", onClick = {

                        })
                    }
                }
            }
        }
    }
}