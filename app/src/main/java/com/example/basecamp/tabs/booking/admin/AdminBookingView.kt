package com.example.basecamp.tabs.booking.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basecamp.UserModel
import com.example.basecamp.UserStatus
import com.example.basecamp.components.CustomButton
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
import com.example.basecamp.tabs.booking.models.BookingCategories
import com.example.basecamp.tabs.booking.models.BookingItem
import kotlin.compareTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingView(
    modifier: Modifier = Modifier,
    userInfo: UserModel?,
    authViewModel: AuthViewModel?,
    adminBookingViewModel: AdminBookingViewModel?,
    goBack: () -> Unit,
    categoryId: String = "",
    navigateToExtra: (String, String, String, String, String) -> Unit = { _, _, _, _, _ -> }
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }

    val categories by adminBookingViewModel?.categories?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val bookingId = "${System.currentTimeMillis()}_${(1000..9999).random()}"

    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        val category = categories.find { it.id == categoryId }
        selectedCategoryName = category?.name ?: ""
    }


    CustomColumn(title = "Add Booking Item") {
        CustomButton(text = "Back", onClick = goBack)

        CategoryDropdown(
            categories = categories,
            expanded = expanded,
            selectedCategoryName = selectedCategoryName,
            onExpandedChange = { expanded = !expanded },
            onCategorySelected = { category ->
                selectedCategoryName = category.name
                selectedCategoryId = category.id
                expanded = false
            }
        )

        BookingItemForm(
            name = name,
            onNameChange = { name = it },
            info = info,
            onInfoChange = { if (it.length <= 500) info = it },
            pricePerDay = pricePerDay,
            onPricePerDayChange = { pricePerDay = it },
            quantity = quantity,
            onQuantityChange = { quantity = it },
            onAddItemClick = {

                val price = pricePerDay
                val quantityValue = quantity

                adminBookingViewModel?.addBookingItem(
                    bookingItem = BookingItem(
                        id = bookingId,
                        pricePerDay = price,
                        name = name,
                        info = info,
                        quantity = quantityValue,
                        createdBy = userInfo?.name ?: ""
                    ),
                    selectedCategory = selectedCategoryId,
                )
                name = ""
                info = ""
                pricePerDay = ""
                selectedCategoryName = ""
                selectedCategoryId = ""
            },
            onAddExtrasClick = {
                navigateToExtra(
                    selectedCategoryId,  // categoryId (1st parameter)
                    name,                // bookingName (2nd parameter)
                    info,                // bookingInfo (3rd parameter)
                    pricePerDay,         // bookingPrice (4th parameter)
                    bookingId            // bookingId (5th parameter)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<BookingCategories>,
    expanded: Boolean,
    selectedCategoryName: String,
    onExpandedChange: () -> Unit,
    onCategorySelected: (BookingCategories) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange() },
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
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { onExpandedChange() }
        ) {
            categories.forEach { category ->
                Text(
                    text = category.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(category) }
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingItemForm(
    quantity: String,
    onQuantityChange: (String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    info: String,
    onInfoChange: (String) -> Unit,
    pricePerDay: String,
    onPricePerDayChange: (String) -> Unit,
    onAddItemClick: () -> Unit,
    onAddExtrasClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Name") },
            value = name,
            onValueChange = onNameChange,
            maxLines = 1
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Info") },
            maxLines = 10,
            minLines = 10,
            value = info,
            onValueChange = onInfoChange,
            supportingText = { Text("${info.length}/500 characters") }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Price per day") },
            value = pricePerDay,
            onValueChange = onPricePerDayChange,
            maxLines = 1,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Quantity") },
            value = quantity,
            onValueChange = onQuantityChange,
            maxLines = 1,
        )
        CustomButton(onClick = onAddItemClick, text = "Add item")
        CustomButton(text = "Add Extras", onClick = onAddExtrasClick)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBookingViewPreview() {
    AdminBookingView(
        userInfo = UserModel(
            name = "",
            email = "",
            imageUrl = null,
            bio = "",
            status = UserStatus.ADMIN,
            id = "",
            companyName = ""
        ),
        authViewModel = null,
        adminBookingViewModel = null,
        goBack = {},
        categoryId = ""
    )


}