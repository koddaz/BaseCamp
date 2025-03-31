package com.basecampers.basecamp.tabs.booking.adminstuff

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.compareTo

@Composable
fun AddBookingItem() {
    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            label = { Text("Name") },
            value = name,
            onValueChange = { name = it },
            maxLines = 1
        )
        OutlinedTextField(
            label = { Text("Info") },
            maxLines = 10,
            minLines = 10,
            value = info,
            onValueChange = { if (it.length <= 500) info = it },
            supportingText = { Text("${info.length}/500 characters") }
        )
        OutlinedTextField(
            label = { Text("Price per day") },
            value = pricePerDay,
            onValueChange = { pricePerDay = it },
            maxLines = 1,

        )
    }

}

class AddItemViewModel : ViewModel() {
    val db = Firebase.firestore

    fun addBookingItem(bookingItem: BookingItem) {
        db.collection("bookingItems").add(bookingItem)
    }

}

class BookingItem(
    val id: Int,
    val pricePerDay: Double,
    val name: String,
    val info: String,

)

@Preview(showBackground = true)
@Composable
fun AddBookingItemPreview() {
    AddBookingItem()
}