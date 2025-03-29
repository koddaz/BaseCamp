package com.basecampers.basecamp.tabs.booking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.booking.models.BookingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComposable(
    onDateRangeSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    Column(modifier = Modifier.fillMaxSize()) {



        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val startDate = dateRangePickerState.selectedStartDateMillis
                        val endDate = dateRangePickerState.selectedEndDateMillis
                        onDateRangeSelected(startDate, endDate)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                headline = null,
                title = null,
                showModeToggle = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(16.dp)
            )
        }
            }

}

@Preview(showBackground = true)
@Composable
fun DatePreview() {
    DatePickerComposable(
        onDateRangeSelected = { _, _ -> },
        onDismiss = {}
    )
}

