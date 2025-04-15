package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.components.CustomButton

@Composable
fun BookingView(onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        CustomButton(text = "Book an item", onClick = onClick )
    }
}
