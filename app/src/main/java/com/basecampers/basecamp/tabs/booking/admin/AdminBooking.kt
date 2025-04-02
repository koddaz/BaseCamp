package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBooking(
    modifier: Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel = viewModel(),
    onClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val categories by adminBookingViewModel.categories.collectAsState()

    val scrollState = rememberScrollState()


    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState)) {

        var pageNumber by remember { mutableStateOf(1) }

        Row(modifier.fillMaxWidth()) {
            CustomButton(text = "USER", onClick = onClick)
            CustomButton(text = "change page", onClick = {
                pageNumber = if (pageNumber == 1) {
                    2
                } else {
                    1
                }
            })
        }

        if(pageNumber == 1) {
            CategoriesView(
                categories = categories,
                adminBookingViewModel = adminBookingViewModel,
                info = info,
                errorMessage = errorMessage,
                onInfoChange = { info = it },
                onErrorChange = { errorMessage = it }
            )
        } else if (pageNumber == 2) {
            AddBookingItem(
                categories = categories,
                name = name,
                info = info,
                pricePerDay = pricePerDay,
                onNameChange = { name = it },
                onInfoChange = { info = it },
                onPricePerDayChange = { pricePerDay = it }, // Changed to accept String directly
                adminBookingViewModel = adminBookingViewModel
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookingItem(
    modifier: Modifier = Modifier,
    name: String = "",
    info: String = "",
    categories: List<BookingCategories> = emptyList(),
    pricePerDay: String = "",
    onNameChange: (String) -> Unit = {},
    onInfoChange: (String) -> Unit = {},
    onPricePerDayChange: (String) -> Unit = {},
    adminBookingViewModel: AdminBookingViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }

    CustomColumn(title = "Add Booking Item") {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a category") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                modifier = modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategoryName = category.name
                                selectedCategoryId = category.id // Store the ID
                                expanded = false
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Name") },
            value = name,
            onValueChange = onNameChange, // Fixed: Call the function with the argument
            maxLines = 1
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Info") },
            maxLines = 10,
            minLines = 10,
            value = info,
            onValueChange = { if (it.length <= 500) onInfoChange(it) }, // Fixed: Call with argument
            supportingText = { Text("${info.length}/500 characters") }
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Price per day") },
            value = pricePerDay,
            onValueChange = { onPricePerDayChange(it) }, // Fixed: Call with argument
            maxLines = 1,
        )


        CustomButton(onClick = {
            val price = pricePerDay.toDoubleOrNull() ?: 0.0
            adminBookingViewModel.addBookingItem(
                bookingItem = BookingItem(
                    id = 0,
                    pricePerDay = price,
                    name = name,
                    info = info
                ),
                selectedCategory = selectedCategoryId // Use ID instead of name
            )
            onNameChange("")
            onInfoChange("")
            onPricePerDayChange("")
            selectedCategoryName = ""
            selectedCategoryId = ""
        }, text = "Add item")
    }
}


@Composable
fun CategoriesView(

    modifier : Modifier = Modifier,
    categories: List<BookingCategories> = emptyList(),
    adminBookingViewModel: AdminBookingViewModel = viewModel(),
    info: String = "",
    errorMessage: String = "",
    onCategoryChange: (String) -> Unit = {},
    onInfoChange: (String) -> Unit = {},
    onErrorChange: (String) -> Unit = {}
) {
    var category by remember { mutableStateOf("") }
    CustomColumn(title = "Categories") {

        CustomColumn() {
            Column(modifier.padding(start = 16.dp)) {
                if (categories.isNotEmpty()) {
                    categories.forEach { category ->
                        Row {
                            Text(
                                text = "• ",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = category.name)
                        }
                    }
                } else {
                    Text(text = "No categories found")
                }
            }
        }

        OutlinedTextField(
            label = { Text("Category") },
            value = category,
            onValueChange = { newCategory ->
                val filteredValue = newCategory.replace("\\s".toRegex(), "")
                category = filteredValue
            },
            maxLines = 1,
            modifier = modifier.fillMaxWidth()
        )
        CustomButton(onClick = {
            if (category.isNotEmpty()) {
                // Generate a unique ID using current timestamp + random suffix
                val categoryId = "${System.currentTimeMillis()}_${(1000..9999).random()}"

                adminBookingViewModel.addBookingCategory(
                    BookingCategories(
                        id = categoryId,
                        name = category,
                        info = info
                    )
                )

                // Reset fields after successful add
                category = ""
                onInfoChange("")
                onErrorChange("")
            } else {
                onErrorChange("Category name is required")
            }
        }, text = "Add category")
    }
}




@Composable
fun CustomColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .padding(16.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = typography.titleLarge)
        }
        content()
    }
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(onClick = onClick) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun AddBookingItemPreview() {
    AdminBooking()
}