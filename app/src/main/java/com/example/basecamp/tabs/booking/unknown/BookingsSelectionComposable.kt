package com.example.basecamp.tabs.booking.unknown

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.booking.models.BookingCatalog
import com.example.basecamp.tabs.booking.models.BookingItems
import com.example.basecamp.tabs.booking.models.BookingViewModel

@Composable
fun BookingSelectionComposable(
    onItemSelected: (BookingItems?) -> Unit,
    modifier: Modifier = Modifier,
    bookingViewModel: BookingViewModel = viewModel(),
) {
    val pagerState = rememberPagerState(pageCount = {
        BookingCatalog.items.size
    })
    val selectedItem = bookingViewModel.selectedBookingItem.collectAsState().value

    Column(modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState
        ) { page ->
            val currentItem = BookingCatalog.items[page]
            val isSelected = selectedItem?.id == currentItem.id
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
                            onItemSelected(currentItem)
                            bookingViewModel.setSelectedBookingItem(currentItem)
                        } else {
                            onItemSelected(null)
                            bookingViewModel.removeSelectedBookingItem()
                        }
                    }
                )
            }
        }
    }
}
