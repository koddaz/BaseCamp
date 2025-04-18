package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExtrasView(
    navOnConfirm: () -> Unit,
    goBack: () -> Unit,
    adminBookingViewModel: AdminBookingViewModel? = viewModel(),
) {
    val scrollState = rememberScrollState()

    var extraName by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var extraPrice by remember { mutableStateOf("") }
    var extraQuantity by remember { mutableStateOf("") }

    val extraList by adminBookingViewModel?.bookingExtras?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val selectedItem by adminBookingViewModel?.selectedItem?.collectAsState()
        ?: remember { mutableStateOf<BookingItem?>(null)
        }
    val selectedCategory by adminBookingViewModel?.selectedCategory?.collectAsState()
        ?: remember { mutableStateOf<BookingCategories?>(null)
        }



    var errorMessage by remember { mutableStateOf("") }

    Column(Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        ) {
    CustomColumn(title = "Add Extra Item") {
        DisplayBookingDetails(
            extraList = extraList,
            categoryName = selectedCategory?.name ?: "",
            bookingName = selectedItem?.name ?: "",
            bookingInfo = selectedItem?.info ?: "",
            bookingPrice = selectedItem?.pricePerDay ?: "",
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
            onSaveClick = {
                selectedItem?.let { item ->
                    adminBookingViewModel?.addBookingWithExtra(
                        bookingItem = item,
                        bookingExtras = extraList,
                        selectedCategory = selectedCategory?.id.toString()
                    )
                    navOnConfirm()
                } ?: run {
                    errorMessage = "No booking item selected"
                }
            },
            onAddExtraClick = {
                when {
                    extraName.isBlank() || extraPrice.isBlank() ->
                        errorMessage = "Extra name and price are required"
                    extraInfo.length > 300 ->
                        errorMessage = "Extra info cannot exceed 300 characters"
                    extraQuantity.isBlank() ->
                        errorMessage = "Extra quantity is required"
                    !extraPrice.all { it.isDigit() } ->
                        errorMessage = "Price must contain only numbers"
                    else -> {
                        selectedItem?.let { item ->



                            try {
                                adminBookingViewModel?.updateExtraValue(
                                    id = "${System.currentTimeMillis()}_${(1000..9999).random()}",
                                    name = extraName,
                                    info = extraInfo,
                                    price = extraPrice
                                )
                                // Add the extra to the parent item
                                adminBookingViewModel?.addExtraList(
                                    listOf(BookingExtra(
                                        id = "${System.currentTimeMillis()}_${(1000..9999).random()}",
                                        name = extraName,
                                        info = extraInfo,
                                        price = extraPrice
                                    )))

                                // Reset form
                                extraName = ""
                                extraInfo = ""
                                extraPrice = ""
                                extraQuantity = ""
                                errorMessage = ""
                                

                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                            }
                        } ?: run {
                            errorMessage = "Parent booking item not found"
                        }
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
}

@Composable
fun DisplayBookingDetails(
    categoryName: String,
    bookingName: String,
    bookingInfo: String,
    bookingPrice: String,
    extraList: List<BookingExtra> = emptyList()) {

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "Category: $categoryName")
            Spacer(modifier = Modifier.weight(1f))
            Column() {
                Text(text = "Name: $bookingName")
                Text(text = "Info: $bookingInfo")
                Text(text = "Price: $bookingPrice")
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Extra items")
            extraList.forEach { extra ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Name: ${extra.name}")
                    Text(text = "Info: ${extra.info}")
                    Text(text = "Price: ${extra.price}")
                }
            }
        }
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
    onSaveClick: () -> Unit,
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
            text = "Add"
        )
        CustomButton(
            text = "Save",
            onClick = {
                onSaveClick()
            }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AdminExtrasViewPreview() {
    AdminExtrasView(
        goBack = {},
        adminBookingViewModel = null,
        navOnConfirm = {}
    )
}


