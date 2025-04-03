package com.example.basecamp.tabs.booking.unknown
/*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.tabs.booking.models.UserBookingViewModel
import com.example.basecamp.tabs.booking.models.ExtraItems
import com.example.basecamp.tabs.booking.models.ExtraItemsCatalog.extraItems

@Composable
fun BookingExtraComposable(
    modifier: Modifier = Modifier,
    bookingViewModel: UserBookingViewModel = viewModel(),
    selectedExtraItems: List<ExtraItems>) {
    LazyColumn(modifier.fillMaxWidth().border(width = 1.dp, color = colorScheme.primary).padding(16.dp)) {
        items(extraItems.size) { index ->
            val currentItem = extraItems[index]
            val isSelected = selectedExtraItems.contains(currentItem)

            Row(modifier.fillMaxWidth()) {
                Column(modifier.weight(1f).padding(16.dp)) {
                    Text(text = currentItem.name)
                    Text(text = currentItem.info)
                    Text(text = "$${currentItem.price}")
                }
                Column(modifier.weight(1f).padding(16.dp)) {
                    Text("Picture")
                }
                Column(modifier.padding(16.dp)) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            if (checked) {
                                bookingViewModel.addExtraItem(currentItem)
                            } else {
                                bookingViewModel.removeExtraItem(currentItem)
                            }
                        }
                    )
                }
            }
        }
    }
}

 */