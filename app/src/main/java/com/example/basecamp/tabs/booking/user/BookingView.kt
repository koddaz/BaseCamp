package com.example.basecamp.tabs.booking.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.booking.DatePickerView
import com.example.basecamp.tabs.booking.admin.BookingCategories
import com.example.basecamp.tabs.booking.admin.CustomButton
import com.example.basecamp.tabs.booking.admin.CustomColumn
import com.example.basecamp.tabs.booking.models.BookingItems
import com.example.basecamp.tabs.booking.models.BookingViewModel

@Composable
fun BookingView(
    modifier: Modifier = Modifier,
    bookingViewModel: BookingViewModel = viewModel(),
    onClick: () -> Unit) {

    val categories by bookingViewModel.categories.collectAsState()
    val bookingItems by bookingViewModel.bookingItemsList.collectAsState()
    val selectedDates by bookingViewModel.formattedDateRange.collectAsState()
    // val selectedBookingItem by bookingViewModel.selectedBookingItem.collectAsState()

    var selectedBookingItem by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        bookingViewModel.retrieveCategories()
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {

        CustomButton(text = "ADMIN", onClick = onClick)

        CustomColumn(title = "Category") {
            Row(modifier.fillMaxWidth()) {
                categories.forEach { category ->
                    CategoryCard(
                        modifier = Modifier.weight(1f),
                        category = category,
                        isSelected = category.id == selectedCategoryId,
                        onClick = {
                            bookingViewModel.loadItemsForCategory(category)
                            selectedCategoryId = category.id
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (bookingItems.isNotEmpty()) {
            CustomColumn(title = categories.find { it.id == selectedCategoryId }?.name ?: "") {
                Column {
                    bookingItems.forEach { item  ->
                        ItemCard(
                            item = item,
                            onClick = {
                                bookingViewModel.setSelectedBookingItem(item)
                                selectedBookingItem = item.name
                            },
                            isSelected = item.name == selectedBookingItem
                        )
                    }
                }
            }
        } else {
            Text(text = "No items available")
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (selectedBookingItem.isNotEmpty()) {
        CustomColumn(title = "Select a date") {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }

            ) {
                Row(modifier.fillMaxWidth().padding(16.dp)) {
                    Text(text = selectedDates)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

            }
        }
            CustomButton(text = "Confirm", onClick = {
                bookingViewModel.createBooking(onFailure = {}, onSuccess = {})
            })
        }

    if (showDatePicker) {
        DatePickerView(
            onDateRangeSelected = { startDate, endDate ->
                bookingViewModel.updateSelectedDateRange(startDate, endDate)
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
    }

}


@Composable
fun ItemCard(
    item: BookingItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected)
                colorScheme.primaryContainer
            else
                colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(text = item.name,
                color = if (isSelected)
                colorScheme.onPrimaryContainer
            else
                colorScheme.onSurface)
            Text(text = item.info,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)
            Text(text = "$${item.price}",
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)

        }
    }
}


@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: BookingCategories,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = if (isSelected)
                    colorScheme.primaryContainer
                else
                    colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(
                text = category.name,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface
            )
        }
    }
}