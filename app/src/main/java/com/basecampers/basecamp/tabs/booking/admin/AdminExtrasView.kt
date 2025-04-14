package com.basecampers.basecamp.tabs.booking.admin

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.UserModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExtrasView(
    modifier: Modifier = Modifier,
    goBack: () -> Unit,
    adminBookingViewModel: AdminBookingViewModel = viewModel(),
    userInfo: UserModel?
) {
    var extraName by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var extraPrice by remember { mutableStateOf("") }
    var extraQuantity by remember { mutableStateOf("") }

    val bookingId by adminBookingViewModel.selectedItemId.collectAsState()
    val categoryId by adminBookingViewModel.selectedCategoryId.collectAsState()
    val bookingName by adminBookingViewModel.itemName.collectAsState()
    val bookingInfo by adminBookingViewModel.itemInfo.collectAsState()
    val bookingPrice by adminBookingViewModel.itemPrice.collectAsState()


    var errorMessage by remember { mutableStateOf("") }

    CustomColumn(title = "Add Extra Item") {
        DisplayBookingDetails(
            categoryId,
            bookingName,
            bookingInfo,
            bookingPrice,
            errorMessage
        )
        ExtraItemForm(
            name = extraName,
            onNameChange = { extraName = it },
            info = extraInfo,
            onInfoChange = { extraInfo = it },
            price = extraPrice,
            onPriceChange = { extraPrice = it },
            quantity = extraQuantity,
            onQuantityChange = { extraQuantity = it },
            onAddExtraClick = {
                // Validate inputs
                if (extraName.isBlank() || extraPrice.isBlank()) {
                    errorMessage = "Extra name and price are required"
                    return@ExtraItemForm
                }
                if (extraInfo.length > 300) {
                    errorMessage = "Extra info cannot exceed 300 characters"
                    return@ExtraItemForm
                }
                if (extraQuantity.isBlank()) {
                    errorMessage = "Extra quantity is required"
                    return@ExtraItemForm
                }
                if (!extraPrice.all { it.isDigit() }) {
                    errorMessage = "Price must contain only numbers"
                    return@ExtraItemForm
                }

                if (errorMessage.isEmpty()) {
                // Debug what's happening
                Log.d("AdminExtrasView", "Adding extra with bookingId: $bookingId")
                Log.d("AdminExtrasView", "Category: $categoryId")
                Log.d("AdminExtrasView", "Booking Name: $bookingName")
                Log.d("AdminExtrasView", "Booking Info: $bookingInfo")

                // Generate IDs if needed
                val newBookingId = if (bookingId.isEmpty()) {
                    "${System.currentTimeMillis()}_${(1000..9999).random()}"
                } else {
                    bookingId
                }

                // Create booking item with correct values
                val bookingItem = BookingItem(
                    id = newBookingId,
                    categoryId = categoryId,
                    pricePerDay = bookingPrice,
                    name = bookingName,
                    info = bookingInfo,
                    quantity = "1",
                    createdBy = userInfo?.id ?: ""
                )

                val extraId = "${System.currentTimeMillis()}_${(1000..9999).random()}"
                val extraItem = BookingExtra(
                    id = extraId,
                    price = extraPrice,
                    name = extraName,
                    info = extraInfo
                )

                try {
                    // Always add or update the booking item
                    adminBookingViewModel.addBookingItem(
                        bookingItem = bookingItem,
                        selectedCategory = categoryId
                    )

                    // Then add the extra
                    adminBookingViewModel.addBookingExtra(
                        bookingItem = bookingItem,
                        bookingExtra = extraItem,
                        selectedCategory = categoryId
                    )

                    // Reset form
                    extraName = ""
                    extraInfo = ""
                    extraPrice = ""
                    extraQuantity = ""
                    errorMessage = ""
                } catch (e: Exception) {
                    errorMessage = "Error: ${e.message}"
                }
            }
            }
        )
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }
        CustomButton(text = "Back", onClick = goBack)



    }
}

@Composable
fun DisplayBookingDetails(categoryId: String, bookingName: String, bookingInfo: String, bookingPrice: String, errorMessage: String) {
    Column {

        Text(text = "Category ID: $categoryId")
        Text(text = "Booking Name: $bookingName")
        Text(text = "Booking Info: $bookingInfo")
        Text(text = "Booking Price: $bookingPrice")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraItemForm(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    info: String,
    onInfoChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    onAddExtraClick: () -> Unit,
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            value = name,
            onValueChange = {
                onNameChange(it) // Call the function with the updated value
            },
            maxLines = 1
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Info") },
            maxLines = 5,
            minLines = 3,
            value = info,
            onValueChange = {

                if (it.length <= 300) {
                    onInfoChange(it)
                }
                     },
            supportingText = { Text("${info.length}/300 characters") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Price") },
            value = price,
            onValueChange = {
                onPriceChange(it)
            },
            maxLines = 1
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantity") },
            value = quantity,
            onValueChange = onQuantityChange,
            maxLines = 1
        )
        CustomButton(
            onClick = {
                if (name.isNotBlank() && price.isNotBlank() && info.isNotBlank() && quantity.isNotBlank()) {
                    onAddExtraClick()
                }
            },
            text = "Add Extra"
        )
    }
}

