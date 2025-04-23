package com.basecampers.basecamp.tabs.booking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import kotlin.collections.forEach

@Composable
fun CategoriesCard(
    modifier: Modifier = Modifier,
    title: String = "",
    info: String = "",
    itemList: List<BookingItem>? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = title)
            Text(text = info)

            if (itemList != null) {
                Text("Items in this category:", style = typography.labelLarge)
                itemList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${item.name} - ${item.pricePerDay}",
                            style = typography.bodyMedium
                        )
                    }
                }
            } else {
                Text("", style = typography.bodySmall)
            }
        }
    }
}
