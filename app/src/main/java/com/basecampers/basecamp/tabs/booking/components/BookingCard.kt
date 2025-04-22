package com.basecampers.basecamp.tabs.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BookingCard(
    modifier: Modifier = Modifier,
    title: String,
    info: String,
    price: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .background
                    (color = if (selected) Color.Green else Color.Transparent)) {
            Text(
                text = title,
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    "Place for picture",
                    style = typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Row() {
                Text(
                    text = info,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = price,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}