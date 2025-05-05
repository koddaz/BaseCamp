package com.basecampers.basecamp.tabs.booking.admin.createBooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExtrasView(
    navOnConfirm: () -> Unit,
    goBack: () -> Unit,
    adminBookingViewModel: AdminBookingViewModel
) {
    val scrollState = rememberScrollState()

    var extraName by remember { mutableStateOf("") }
    var extraInfo by remember { mutableStateOf("") }
    var extraPrice by remember { mutableStateOf("") }
    var extraQuantity by remember { mutableStateOf("") }

    val extraList by adminBookingViewModel.bookingExtras.collectAsState()
    val selectedItem by adminBookingViewModel.selectedItem.collectAsState()
    val selectedCategory by adminBookingViewModel.selectedCategory.collectAsState()

    var errorMessage by remember { mutableStateOf("") }

    // Use a single scrollable column for the entire content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SecondaryAqua.copy(alpha = 0.2f),
                        AppBackground
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Header
        Text(
            text = "Bookings",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Booking Details Card
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DisplayBookingDetails(
                    extraList = extraList,
                    categoryName = selectedCategory?.name ?: "",
                    bookingName = selectedItem?.name ?: "",
                    bookingInfo = selectedItem?.info ?: "",
                    bookingPrice = selectedItem?.pricePerDay ?: "",
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Extra Item Form Card
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Name field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    label = { Text("Name") },
                    value = extraName,
                    onValueChange = { extraName = it },
                    maxLines = 1
                )

                // Info field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    label = { Text("Info") },
                    maxLines = 5,
                    minLines = 3,
                    value = extraInfo,
                    onValueChange = {
                        if (it.length <= 300) {
                            extraInfo = it
                        }
                    },
                    supportingText = { Text("${extraInfo.length}/300 characters") }
                )

                // Price field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    label = { Text("Price") },
                    value = extraPrice,
                    onValueChange = { extraPrice = it },
                    maxLines = 1
                )

                // Quantity field
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    label = { Text("Quantity") },
                    value = extraQuantity,
                    onValueChange = { extraQuantity = it },
                    maxLines = 1
                )

                // Add Extra button
                CustomButton(
                    onClick = {
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
                                        adminBookingViewModel.updateExtraValue(
                                            id = "${System.currentTimeMillis()}_${(1000..9999).random()}",
                                            name = extraName,
                                            info = extraInfo,
                                            price = extraPrice
                                        )
                                        // Add the extra to the parent item
                                        adminBookingViewModel.addExtraList(
                                            listOf(
                                                BookingExtra(
                                                    id = "${System.currentTimeMillis()}_${(1000..9999).random()}",
                                                    name = extraName,
                                                    info = extraInfo,
                                                    price = extraPrice
                                                )
                                            )
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
                                } ?: run {
                                    errorMessage = "Parent booking item not found"
                                }
                            }
                        }
                    },
                    text = "Add Extra",
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Save button
                CustomButton(
                    text = "Save All",
                    onClick = {
                        selectedItem?.let { item ->
                            adminBookingViewModel.addBookingWithExtra(
                                bookingItem = item,
                                bookingExtras = extraList,
                                selectedCategory = selectedCategory?.id.toString()
                            )
                            navOnConfirm()
                        } ?: run {
                            errorMessage = "No booking item selected"
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Back button
        CustomButton(
            text = "Back",
            onClick = goBack,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun DisplayBookingDetails(
    categoryName: String,
    bookingName: String,
    bookingInfo: String,
    bookingPrice: String,
    extraList: List<BookingExtra> = emptyList()
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(text = "Category: $categoryName")
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Text(text = "Name: $bookingName")
                Text(text = "Info: $bookingInfo")
                Text(text = "Price: $bookingPrice")
            }
        }

        if (extraList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Extra items:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            extraList.forEach { extra ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "Name: ${extra.name}")
                        Text(text = "Info: ${extra.info}")
                        Text(text = "Price: ${extra.price}")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminExtrasViewPreview() {
    AdminExtrasView(
        goBack = {},
        adminBookingViewModel = viewModel(),
        navOnConfirm = {}
    )
}