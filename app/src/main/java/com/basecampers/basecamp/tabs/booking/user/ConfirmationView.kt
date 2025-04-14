package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn

import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel

@Composable
fun ConfirmationView(
    bookingViewModel: UserBookingViewModel,
    confirmBooking: () -> Unit,
    ) {

    val selectedCategory by bookingViewModel.categories.collectAsState()
    val selectedItem by bookingViewModel.selectedBookingItem.collectAsState()
    val selectedExtraItems by bookingViewModel.selectedExtraItems.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        CustomColumn {

            Text(text = "Selected Category: ${selectedCategory.firstOrNull()?.name ?: "None"}")
            Text(text = "Selected Item: ${selectedItem?.name ?: "None"}")
            Text(text = "Selected Extras:")

            if (selectedExtraItems.isEmpty()) {
                Text(text = "No extras selected")
            } else {
                selectedExtraItems.forEach { extra ->
                    Text(text = "- ${extra.name}")
                }
            }

            CustomButton(
                text = "Confirm Booking",
                onClick = confirmBooking
            )
        }
    }




}