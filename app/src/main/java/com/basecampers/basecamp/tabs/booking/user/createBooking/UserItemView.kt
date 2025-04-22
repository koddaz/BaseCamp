package com.basecampers.basecamp.tabs.booking.user.createBooking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.user.components.DatePickerView
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserItemView(
    bookingViewModel: UserBookingViewModel?,
    navExtra: (String) -> Unit,
) {

    val scrollState = rememberScrollState()
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val formattedDateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState() ?: remember { mutableStateOf<BookingItem?>(null) }
    val itemList by bookingViewModel?.bookingItemsList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
            itemList.forEach { item ->
                BookingCard(
                    selected = item.id == selectedItem?.id,
                    title = item.name,
                    info = item.info,
                    price = item.pricePerDay,
                    onClick = {
                        bookingViewModel?.setSelectedBookingItem(item)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formattedDateRange,
                onValueChange = {},
                label = { Text("Date Range") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Range",
                        modifier = Modifier.clickable {
                            showDatePicker = !showDatePicker
                        }
                    )
                },
            )
            if (showDatePicker) {
                DatePickerView(
                    startDate = startDate,
                    endDate = endDate,
                    onDateRangeSelected = { startDate, endDate ->
                        bookingViewModel?.updateDateRange(startDate, endDate)
                    },
                    onDismiss = {
                        showDatePicker = !showDatePicker
                    }


                )
            }
            CustomButton(text = "Next", onClick = {
                selectedItem?.let { item ->
                    bookingViewModel?.retrieveExtraItems(item.categoryId, item.id)
                    navExtra(item.id)
                }

            })
        }
    }

}