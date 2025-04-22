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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Booking Summary Section
            item {
                Text(
                    text = "Booking Summary",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                BasecampCard(
                    title = "Selected Item",
                    subtitle = selectedBookingItem?.name ?: "",
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedBookingItem?.info ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Date Range: $formattedDateRange",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Duration: $amountOfDays days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SecondaryAqua.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Base Price: €${selectedBookingItem?.pricePerDay}/day",
                                style = MaterialTheme.typography.titleSmall,
                                color = SecondaryAqua,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Selected Extras Section
            if (selectedExtraItems.isNotEmpty()) {
                item {
                    Text(
                        text = "Selected Extras",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                items(selectedExtraItems) { extra ->
                    ExtrasCard(
                        extra = extra,
                        isSelected = true,
                        onClick = {
                            bookingViewModel?.removeExtraItem(extra)
                            bookingViewModel?.calculateExtra(extraItems)
                        }
                    )
                }
            }

            // Available Extras Section
            item {
                Text(
                    text = "Available Extras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (extraItems.isNotEmpty()) {
                items(extraItems.filter { it !in selectedExtraItems }) { extra ->
                    ExtrasCard(
                        extra = extra,
                        isSelected = false,
                        onClick = {
                            bookingViewModel?.addExtraItem(extra)
                            bookingViewModel?.calculateExtra(extraItems)
                        }
                    )
                }
            } else {
                item {
                    BasecampCard(
                        title = "No Extras",
                        subtitle = "No extra items available",
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "No additional items available for this booking",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // Total Price Section
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = SecondaryAqua
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Price",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "€${String.format(Locale.getDefault(), "%.2f", totalPrice)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Button(
                    onClick = navConfirmation,
                    modifier = Modifier.fillMaxWidth(),
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