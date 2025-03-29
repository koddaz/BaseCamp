package com.basecampers.basecamp.tabs.booking

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.tabs.booking.models.BookingViewModel
import com.basecampers.components.NavButton

@Composable
fun BookingExtra(
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onBack: () -> Unit,
    bookingViewModel: BookingViewModel = viewModel(),
) {
    // Track selected items from the ViewModel
    val selectedExtraItems = bookingViewModel.seleectedExtraItems.collectAsState().value
    val selectedItem = bookingViewModel.selectedBookingItem.collectAsState().value
    Column(modifier.fillMaxSize()) {
        Column(modifier.weight(1f).padding(16.dp)) {
            Column(modifier.fillMaxWidth().border(width = 1.dp, color = colorScheme.primary).padding(16.dp)) {
                Text("Selected Booking Item")
                Text(text = selectedItem?.name ?: "No item selected")
            }
            Spacer(modifier.height(16.dp))
            Column(modifier.fillMaxWidth().border(width = 1.dp, color = colorScheme.primary).padding(16.dp)) {
                Text("Selected Extra Items")
                if (selectedExtraItems.isEmpty()) {
                    Text("No extra items added...")
                } else {
                    selectedExtraItems.forEach { item ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "${item.name}: $${item.price}",
                            )
                        }
                    }
                }
            }
            Spacer(modifier.height(16.dp))
BookingExtraComposable(bookingViewModel = bookingViewModel, selectedExtraItems = selectedExtraItems)
        }
        Row(modifier.fillMaxWidth().padding(16.dp)) {
            Row() {
                Column(modifier.weight(1f)){
                    NavButton(
                        title = "Back",
                        onClick = { onBack() }
                    )
                }
                Column(modifier.weight(1f)){
                    NavButton(
                        title = "Next",
                        onClick = { onNext() }
                    )
                }
            }



        }
    }
}



@Preview(showBackground = true)
@Composable
fun BookingExtraPreview() {
    BookingExtra(onNext = {}, onBack = {})
}
