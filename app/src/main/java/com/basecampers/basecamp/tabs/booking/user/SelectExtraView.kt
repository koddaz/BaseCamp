package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import java.util.Locale

@Composable
fun SelectExtraView(
    bookingViewModel: UserBookingViewModel?,
    modifier: Modifier = Modifier) {

    val selectedBookingItem by bookingViewModel?.selectedBookingItem?.collectAsState() ?: remember { mutableStateOf<BookingItem?>(null) }
    val formattedDateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    val amountOfDays by bookingViewModel?.amountOfDays?.collectAsState() ?: remember { mutableStateOf(0) }
    val extraItems by bookingViewModel?.bookingExtraList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Column(modifier = modifier.fillMaxSize()) {
        CustomColumn(title = "Booking Summary") {
            selectedBookingItem?.let { item ->
                Text(text = "Selected Item: ${item.name}")
                Text(text = "Date Range: $formattedDateRange")
                Text(text = "Total Days: $amountOfDays")
                val totalPrice = selectedBookingItem?.let { item ->
                    if (amountOfDays > 0) {
                        val pricePerDay = item.pricePerDay.toDoubleOrNull() ?: 0.0
                        "€${String.format(Locale.getDefault(), "%.2f", pricePerDay * amountOfDays)}"
                    } else "FREE!"
                } ?: "FREE!"
                Text(
                    text = "Total price: $totalPrice",
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
        CustomColumn(title = "Extra Items") {
            if (extraItems.isNotEmpty()) {

                extraItems.forEach { extra ->
                    ExtrasCard(
                        extra = extra,
                        onClick = {
                            bookingViewModel?.addExtraItem(
                                BookingExtra(
                                    extra.id, extra.name, extra.price, extra.info
                                )
                            )
                        }
                    )
                }
            }
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
                text = "Price: €${extra.price}",
                style = typography.bodyLarge
            )
        }
    }
}