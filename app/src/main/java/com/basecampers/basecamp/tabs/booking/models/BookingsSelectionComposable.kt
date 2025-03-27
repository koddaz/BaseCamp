package com.basecampers.basecamp.tabs.booking.models

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.basecampers.booking.BookingCatalog
import com.basecampers.booking.BookingItems

@Composable
fun BookingSelectionComposable(
    onItemSelected: (BookingItems?) -> Unit,
    modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = {
        BookingCatalog.items.size
    })
    var selectedItemId by remember { mutableStateOf<Int?>(null) }

    Column(modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            val currentItem = BookingCatalog.items[page]
            val isSelected = selectedItemId == currentItem.id
            Column() {
                Row(modifier.fillMaxWidth().border(1.dp, Color.Black).padding(16.dp)) {
                    Column(modifier.weight(1f)) {
                        Text(text = currentItem.name)
                        Text(text = currentItem.info)
                        Text(text = "$${currentItem.price}")
                    }
                    Column(modifier.weight(1f)) {
                        Text("Picture")
                    }


                }
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked ->
                        if (checked) {
                            selectedItemId = currentItem.id
                            onItemSelected(currentItem)
                        } else {
                            selectedItemId = null
                            onItemSelected(null)
                        }
                    }
                )
            }
        }
    }
}
