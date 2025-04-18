package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import java.util.Locale

@Composable
fun SelectExtraView(
    selectedExtraItems: List<BookingExtra>,
    selectedBookingItem: BookingItem?,
    formattedDateRange: String,
    amountOfDays: Int,
    extraItems: List<BookingExtra>,
    totalPrice: Double,
    navConfirmation: () -> Unit,
    bookingViewModel: UserBookingViewModel?,
    modifier: Modifier = Modifier) {



    Column(modifier = modifier.fillMaxSize()) {
        CustomColumn(title = "Booking Summary") {
            selectedBookingItem?.let { item ->
                Text(text = "Selected Item: ${item.name}")
                Text(text = "Date Range: $formattedDateRange")
                Text(text = "Total Days: $amountOfDays")
                if (totalPrice > 0) {
                    "€${String.format(Locale.getDefault(), "%.2f", totalPrice)}"
                } else "FREE!"
                Text(
                    text = "Total price: $totalPrice",
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            selectedExtraItems.forEach { extra ->
                Text(text = "Selected Extra: ${extra.name}")
                Text(text = "Price: ${extra.price}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomColumn(title = "Extra Items") {
            if (extraItems.isNotEmpty()) {

                extraItems.forEach { extra ->
                    ExtrasCard(
                        extra = extra,
                        onClick = {
                            if (selectedExtraItems.contains(extra)) {
                                bookingViewModel?.removeExtraItem(extra.id)
                            } else {
                                bookingViewModel?.addExtraItem(extra)
                                bookingViewModel?.calculateExtra(extraItems)
                            }
                        }
                    )
                }
            }
        }

        Row() {
            CustomButton(
                text = "Back",
                onClick = {
                    bookingViewModel?.clearSelectedExtras()
                }
            )
            CustomButton(
                text = "Next",
                onClick = {
                    navConfirmation()
                }
            )
        }
    }
}

@Composable
fun ExtrasCard(
    extra: BookingExtra,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = { onClick() })
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            Text(
                text = extra.name,
                style = typography.titleMedium
            )
            Text(
                text = extra.info,
                style = typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = extra.price,
                style = typography.bodyLarge
            )
        }
    }
}