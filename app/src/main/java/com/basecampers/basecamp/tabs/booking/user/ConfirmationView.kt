package com.basecampers.basecamp.tabs.booking.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBooking
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

@Composable
fun ConfirmationView(
    bookingViewModel: UserBookingViewModel?,
    confirmBooking: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    
    // Collect all necessary states
    val selectedBookingItem = bookingViewModel?.selectedBookingItem?.collectAsState()?.value
    val selectedExtraItems = bookingViewModel?.selectedExtraItems?.collectAsState()?.value ?: emptyList()
    val formattedDateRange = bookingViewModel?.formattedDateRange?.collectAsState()?.value ?: ""
    val amountOfDays = bookingViewModel?.amountOfDays?.collectAsState()?.value ?: 0
    val startDate = bookingViewModel?.startDate?.collectAsState()?.value
    val endDate = bookingViewModel?.endDate?.collectAsState()?.value
    val user = bookingViewModel?.user?.collectAsState()?.value
    
    // Calculate total price using a remember calculation
    val calculatedTotalPrice = remember(selectedBookingItem, selectedExtraItems, amountOfDays) {
        val basePrice = selectedBookingItem?.pricePerDay?.toDoubleOrNull() ?: 0.0
        val extrasTotal = selectedExtraItems.sumOf { it.price.toDoubleOrNull() ?: 0.0 }
        (basePrice * (amountOfDays.toDouble())) + extrasTotal
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(SecondaryAqua.copy(alpha = 0.1f))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SecondaryAqua,
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Confirm Your Booking",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Please review your booking details before confirming",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Booking Details Card
                BasecampCard(
                    title = "Booking Details",
                    subtitle = "Summary of your reservation",
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        selectedBookingItem?.let { item ->
                            Text(
                                text = "Item: ${item.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                            Text(
                                text = "Base Price: €${item.pricePerDay}/day",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray.copy(alpha = 0.2f)
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
                    }
                }

                // Selected Extras Card
                if (selectedExtraItems.isNotEmpty()) {
                    BasecampCard(
                        title = "Selected Extras",
                        subtitle = "Additional items for your booking",
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedExtraItems.forEach { extra ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = extra.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "€${extra.price}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Total Price Card
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
                            text = "€${String.format(Locale.getDefault(), "%.2f", calculatedTotalPrice)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Confirm Button
                Button(
                    onClick = {
                        isLoading = true
                        // Get current user ID from Firebase Auth
                        val userId = Firebase.auth.currentUser?.uid
                        val companyId = user?.companyId

                        if (userId != null && companyId != null && selectedBookingItem != null && 
                            startDate != null && endDate != null) {
                            val userBooking = UserBooking(
                                userId = userId,
                                companyId = companyId,
                                bookingItem = selectedBookingItem,
                                extraItems = selectedExtraItems,
                                startDate = startDate,
                                endDate = endDate,
                                totalPrice = calculatedTotalPrice,
                                createdAt = System.currentTimeMillis()
                            )

                            bookingViewModel.saveUserBooking(userBooking)
                            isLoading = false
                            confirmBooking()
                        } else {
                            isLoading = false
                            Log.e("ConfirmationView", "Missing required booking information")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryAqua,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading && selectedBookingItem != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirm Booking",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}