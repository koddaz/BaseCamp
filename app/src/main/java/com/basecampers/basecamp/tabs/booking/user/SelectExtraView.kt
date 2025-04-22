package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.tabs.booking.components.ExtrasCard
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*
import java.util.*

@Composable
fun SelectExtraView(
    modifier: Modifier = Modifier,
    bookingViewModel: UserBookingViewModel?,
    navConfirmation: () -> Unit,
    selectedExtraItems: List<BookingExtra>,
    selectedBookingItem: BookingItem?,
    formattedDateRange: String,
    amountOfDays: Int,
    extraItems: List<BookingExtra>,
    totalPrice: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Booking Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Booking Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = selectedBookingItem?.name ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = formattedDateRange,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "€${selectedBookingItem?.pricePerDay}/day",
                        style = MaterialTheme.typography.titleMedium,
                        color = SecondaryAqua
                    )
                }
            }
        }

        // Available Extras
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Available Extras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(extraItems) { extra ->
                ExtrasCard(
                    extra = extra,
                    isSelected = selectedExtraItems.contains(extra),
                    onClick = {
                        if (selectedExtraItems.contains(extra)) {
                            bookingViewModel?.removeExtraItem(extra)
                        } else {
                            bookingViewModel?.addExtraItem(extra)
                        }
                        bookingViewModel?.calculateExtra(extraItems)
                    }
                )
            }
        }

        // Total and Proceed Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppBackground,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "€${String.format(Locale.getDefault(), "%.2f", totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SecondaryAqua,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { navConfirmation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryAqua
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Proceed to Confirmation",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}