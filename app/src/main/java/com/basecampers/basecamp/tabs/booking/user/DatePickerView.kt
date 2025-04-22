package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape


@Composable
fun DatePickerView(
    onDateRangeSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit,
    startDate: Long?,
    endDate: Long?
) {
    var localStartDate by remember { mutableStateOf(startDate) }
    var localEndDate by remember { mutableStateOf(endDate) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Month navigation header

        // Horizontal month scroll
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Show several months side by side
            items(12) { monthOffset ->
                MonthCalendarView(
                    monthOffset = monthOffset,
                    startDate = localStartDate,
                    endDate = localEndDate,
                    onDateSelected = { date ->
                        // Handle date selection logic
                        when {
                            localStartDate == null -> {
                                localStartDate = date
                                onDateRangeSelected(date, null)
                            }
                            localEndDate == null && date >= localStartDate!! -> {
                                localEndDate = date
                                onDateRangeSelected(localStartDate, date)
                            }
                            else -> {
                                localStartDate = date
                                localEndDate = null
                                onDateRangeSelected(date, null)
                            }
                        }
                    },
                    modifier = Modifier.width(320.dp)
                )
            }
        }
    }
}

@Composable
fun MonthCalendarView(
    monthOffset: Int,
    startDate: Long?,
    endDate: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    val currentCalendar = remember { Calendar.getInstance() }

    // Move calendar to the first day of the specified month offset
    calendar.add(Calendar.MONTH, monthOffset)
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    // Get the month and year to display in header
    val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    val year = calendar.get(Calendar.YEAR)

    Column(modifier = modifier.padding(8.dp)) {
        // Month and year header
        Text(
            text = "$month $year",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Calculate first day of week (0 = Sunday, 1 = Monday, etc)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 1

        // Get days in month
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Calendar grid
        val rows = (firstDayOfMonth + daysInMonth + 6) / 7 // Calculate number of rows needed

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val day = row * 7 + col - firstDayOfMonth + 1

                    if (day in 1..daysInMonth) {
                        // Create date for this cell
                        val cellCalendar = Calendar.getInstance()
                        cellCalendar.time = calendar.time
                        cellCalendar.set(Calendar.DAY_OF_MONTH, day)

                        val cellTimeInMillis = cellCalendar.timeInMillis
                        val isSelected = startDate == cellTimeInMillis || endDate == cellTimeInMillis
                        val isInRange = startDate != null && endDate != null &&
                                cellTimeInMillis in startDate..endDate
                        val isToday = isSameDay(cellCalendar, currentCalendar)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isInRange -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(cellTimeInMillis) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                    isToday -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    } else {
                        // Empty box for padding days
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to check if two dates are the same day
private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}




@Preview(showBackground = true)
@Composable
fun DatePreview() {
    DatePickerView(
        onDateRangeSelected = { _, _ -> },
        onDismiss = {},
        startDate = 123,
        endDate = 321
    )
}

