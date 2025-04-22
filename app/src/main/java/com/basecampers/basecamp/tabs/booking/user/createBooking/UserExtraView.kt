package com.basecampers.basecamp.tabs.booking.user.createBooking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserExtraItem(
    bookingViewModel: UserBookingViewModel,
    navBooking: (String) -> Unit = {},
) {
    val scrollState = rememberScrollState()
    val selectedExtraItems by bookingViewModel.selectedExtraItems.collectAsState()
    val extraList by bookingViewModel.bookingExtraList.collectAsState()

    if (extraList.isEmpty()) {
        UserConfirmationView(
            bookingViewModel = bookingViewModel,
            navBooking = navBooking
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            extraList.forEach { extra ->
                BookingCard(
                    selected = selectedExtraItems.any { it.id == extra.id },
                    title = extra.name,
                    info = extra.info,
                    price = extra.price,
                    onClick = {
                        bookingViewModel.addExtraItem(extra)
                    })
            }
            CustomButton(text = "Next", onClick = {
                bookingViewModel.updatePriceCalculation()
                navBooking
            })
        }
    }
}