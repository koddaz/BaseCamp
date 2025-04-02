package com.basecampers.basecamp.tabs.booking.unknown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.booking.models.BookingViewModel
import com.basecampers.components.NavButton

@Composable
fun BookingCornfirmation(
    onComplete: () -> Unit,
    onBack: () -> Unit,
    bookingViewModel: BookingViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {

    val selectedBookingItem = bookingViewModel.selectedBookingItem.collectAsState().value
    val selectedExtraItems = bookingViewModel.seleectedExtraItems.collectAsState().value
    val formattedDateRange = bookingViewModel.formattedDateRange.collectAsState().value

    val totalPrice = selectedBookingItem?.price?.plus(selectedExtraItems.sumOf { it.price }) ?: 0.0


    Column(modifier.fillMaxSize()) {

        Column(modifier.weight(1f).padding(16.dp)) {
            Text(text = "Booking Confirmation")
            Text(text = "Selected Date Range: ${formattedDateRange}")
            Text(text = "Selected Booking Item: ${selectedBookingItem?.name}")
            Text(text = "Selected Extra Items:")
            selectedExtraItems.forEach { item ->
                Text(text = "- ${item.name}")
            }
            Text(text = "Total Price: $${totalPrice}")
        }

        Row(modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier.weight(1f)) {
                NavButton(
                    title = "Back",
                    onClick = { onBack() },
                )
            }
            Column(modifier.weight(1f)) {
                NavButton(
                    title = "Complete",
                    onClick = {
                        onComplete()
                              },
                )
            }
        }


    }

}
