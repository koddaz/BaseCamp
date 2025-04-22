package com.basecampers.basecamp.tabs.booking.user.createBooking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import kotlin.collections.forEach

@Composable
fun UserConfirmationView(
    bookingViewModel: UserBookingViewModel,
    navBooking: (String) -> Unit) {

    val selectedItem by bookingViewModel.selectedBookingItem.collectAsState()
    val selectedExtraItems by bookingViewModel.selectedExtraItems.collectAsState()
    val formattedDateRange by bookingViewModel.formattedDateRange.collectAsState()
    val totalPrice by bookingViewModel.finalPrice.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        CustomColumn() {
            BookingCard(
                title = selectedItem?.name ?: "",
                info = selectedItem?.info ?: "",
                price = totalPrice.toString(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Date Range: $formattedDateRange")

            Spacer(modifier = Modifier.height(8.dp))


            selectedExtraItems.forEach { extra ->
                Card {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                // Use the correct property names for BookingItem
                                Text(text = "Selected Extra: ${extra.name}")  // Adjust property name
                                Text(text = "Price: ${extra.price }")  // Adjust property name
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.clickable {
                                    // Use the correct property for ID
                                    extra.id.let { itemId ->  // Adjust property name
                                        bookingViewModel.updatePriceCalculation()
                                        bookingViewModel.removeExtraItem(itemId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomButton(text = "Clear", onClick = {
                bookingViewModel.clearAllValues()
                navBooking("")
            })
            CustomButton(text = "Confirm", onClick = {
                bookingViewModel.saveBooking()
            })
        }
    }


}