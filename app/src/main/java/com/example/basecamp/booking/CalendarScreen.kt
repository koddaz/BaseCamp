package com.basecampers.booking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen() {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val datePickerState = rememberDatePickerState()

    DatePicker(
        state = datePickerState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        // dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        title = { Text("Select Date", style = MaterialTheme.typography.titleLarge) },
        headline = { Text("Choose a date for your booking", style = MaterialTheme.typography.bodyLarge) },
        showModeToggle = true,
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}