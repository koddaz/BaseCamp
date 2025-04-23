package com.basecampers.basecamp.tabs.booking.user.bookingOverview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.booking.admin.viewModel.BookingStatus
import com.basecampers.basecamp.tabs.booking.models.UserBookingModel
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun UserCurrentBookings(
    bookingViewModel: UserBookingViewModel,
    goBack: () -> Unit
) {
    val currentBookings by bookingViewModel.currentBookings.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = goBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "My Bookings",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        // Status Tabs
        val bookingsByStatus = currentBookings.groupBy { it.status }
        
        // Pending Bookings
        if (bookingsByStatus[BookingStatus.PENDING]?.isNotEmpty() == true) {
            StatusSection(
                title = "Pending Bookings",
                count = bookingsByStatus[BookingStatus.PENDING]?.size ?: 0,
                statusColor = Color.Yellow.copy(alpha = 0.3f),
                bookings = bookingsByStatus[BookingStatus.PENDING] ?: emptyList(),
                bookingViewModel = bookingViewModel,
                isEditable = true
            )
        }

        // Confirmed Bookings
        if (bookingsByStatus[BookingStatus.CONFIRMED]?.isNotEmpty() == true) {
            StatusSection(
                title = "Confirmed Bookings",
                count = bookingsByStatus[BookingStatus.CONFIRMED]?.size ?: 0,
                statusColor = Color.Green.copy(alpha = 0.3f),
                bookings = bookingsByStatus[BookingStatus.CONFIRMED] ?: emptyList(),
                bookingViewModel = bookingViewModel
            )
        }

        // Canceled Bookings
        if (bookingsByStatus[BookingStatus.CANCELED]?.isNotEmpty() == true) {
            StatusSection(
                title = "Canceled Bookings",
                count = bookingsByStatus[BookingStatus.CANCELED]?.size ?: 0,
                statusColor = Color.Red.copy(alpha = 0.3f),
                bookings = bookingsByStatus[BookingStatus.CANCELED] ?: emptyList(),
                bookingViewModel = bookingViewModel
            )
        }

        if (currentBookings.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = "No Bookings",
                    tint = TextSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Bookings Found",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Text(
                    text = "You haven't made any bookings yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun StatusSection(
    title: String,
    count: Int,
    statusColor: Color,
    bookings: List<UserBookingModel>,
    bookingViewModel: UserBookingViewModel,
    isEditable: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = statusColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Bookings List
        bookings.forEach { booking ->
            var isExpanded by remember { mutableStateOf(false) }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                color = statusColor.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Main Booking Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = booking.bookingItem,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(booking.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        IconButton(
                            onClick = { isExpanded = !isExpanded }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = TextSecondary
                            )
                        }
                    }

                    // Expanded Details
                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = statusColor.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Extra Items
                        if (booking.extraItems.isNotEmpty()) {
                            Text(
                                text = "Extra Items",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            booking.extraItems.forEach { extra ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = extra.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "$${extra.price}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = SecondaryAqua
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Total Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Price",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", booking.totalPrice)}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SecondaryAqua
                            )
                        }

                        // Edit Button for Pending Bookings
                        if (isEditable) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    bookingViewModel.setSelectedBooking(booking)
                                    // Navigate to edit view
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (statusColor == Color.Yellow.copy(alpha = 0.3f)) {
                                        SecondaryAqua
                                    } else {
                                        statusColor.copy(alpha = 0.8f)
                                    },
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Edit Booking")
                            }
                        }
                    }
                }
            }
        }
    }
}