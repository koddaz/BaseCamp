package com.example.basecamp.tabs.booking.unknown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.booking.DatePickerView
import com.example.basecamp.tabs.booking.models.BookingItems
import com.example.basecamp.tabs.booking.models.BookingViewModel
import com.example.components.NavButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bookingViewModel: BookingViewModel = viewModel()
    ) {

    val formattedDateRange by bookingViewModel.formattedDateRange.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<BookingItems?>(null) }

    Column(modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(modifier.weight(1f)) {
            BookingSelectionComposable(
                bookingViewModel = bookingViewModel,
                onItemSelected = { item ->
                    selectedItem = item
                }
            )
            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
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
                    onDateRangeSelected = { startDate, endDate ->
                        bookingViewModel.updateSelectedDateRange(startDate, endDate)
                    },
                    onDismiss = {
                        showDatePicker = !showDatePicker
                    }


                )
            }
        }
        Row(modifier.fillMaxWidth()) {
            NavButton(
                title = "Next",
                onClick = { onNext() }
            )
            NavButton(
                title = "Back",
                onClick = { onBack() }
            )
        }
    }
}







@Preview(showBackground = true)
@Composable
fun BookingsPreview() {
    BookingScreen(onNext = {}, onBack = {})
}