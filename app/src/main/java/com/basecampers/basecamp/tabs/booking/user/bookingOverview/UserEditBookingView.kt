package com.basecampers.basecamp.tabs.booking.user.bookingOverview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserEditBookingView(
    bookingViewModel: UserBookingViewModel,
    goBack: () -> Unit,
    navConfirm: () -> Unit
) {
    val selectedBooking by bookingViewModel.selectedBooking.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {

        selectedBooking?.extraItems?.forEach { extra ->
            Row(modifier = Modifier.fillMaxSize()) {
                Column() {
                    Text(extra.name)
                    Text(extra.price)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    modifier = Modifier.clickable {
                        bookingViewModel.removeExtraItem(extra)
                    })
            }
        }
    }
}