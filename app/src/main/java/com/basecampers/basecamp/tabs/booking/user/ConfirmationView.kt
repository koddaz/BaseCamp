package com.basecampers.basecamp.tabs.booking.user

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.UserBooking

import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ConfirmationView1(
    bookingViewModel: UserBookingViewModel,
    confirmBooking: () -> Unit,

    ) {

    val selectedCategory by bookingViewModel.categoriesList.collectAsState()
    val selectedItem by bookingViewModel.selectedBookingItem.collectAsState()
    val selectedExtraItems by bookingViewModel.selectedExtraItems.collectAsState()
    val totalPrice by bookingViewModel.finalPrice.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        CustomColumn {

            Text(text = "Selected Category: ${selectedCategory.firstOrNull()?.name ?: "None"}")
            Text(text = "Selected Item: ${selectedItem?.name ?: "None"}")
            Text(text = "Selected Extras:")

            if (selectedExtraItems.isEmpty()) {
                Text(text = "No extras selected")
            } else {
                selectedExtraItems.forEach { extra ->
                    Text(text = "- ${extra.name}")
                }
            }
            Text("Total Price: $totalPrice")

            CustomButton(
                text = "Confirm Booking",
                onClick = {
                    // Get current user ID from Firebase Auth
                    val userId = Firebase.auth.currentUser?.uid
                    // Get company ID from the viewModel
                    val companyId = bookingViewModel.user.value?.companyId

                    if (userId != null && companyId != null) {
                        val userBooking = UserBooking(
                            userId = userId.toString(),
                            companyId = companyId,
                            bookingItem = selectedItem,
                            extraItems = selectedExtraItems,
                            startDate = bookingViewModel.startDate.value,
                            endDate = bookingViewModel.endDate.value,
                            totalPrice = bookingViewModel.finalPrice.value,
                            createdAt = System.currentTimeMillis()
                        )

                        bookingViewModel.saveUserBooking(userBooking)
                        confirmBooking()
                    } else {
                        Log.e("ConfirmationView", "Missing userId or companyId")
                    }
                }
            )
        }
    }




}