package com.basecampers.basecamp.tabs.booking.admin

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
import com.basecampers.basecamp.CompanyProfileModel
import com.basecampers.basecamp.UserStatus
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingView(
    modifier: Modifier = Modifier,
    userInfo: CompanyProfileModel?,
    authViewModel: AuthViewModel?,
    adminBookingViewModel: AdminBookingViewModel?,
    goBack: () -> Unit,
    categoryId: String = "",
    navigateToExtra: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryIdFromVM by adminBookingViewModel?.selectedCategoryId?.collectAsState() ?: remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(categoryId.ifEmpty { selectedCategoryIdFromVM }) }
    val selectedBookingId by adminBookingViewModel?.selectedItemId?.collectAsState() ?: remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }

    val categories by adminBookingViewModel?.categories?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val bookingId = "${System.currentTimeMillis()}_${(1000..9999).random()}"

    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    LaunchedEffect(selectedCategoryId, categories) {
        val category = categories.find { it.id == selectedCategoryId }
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
                if (name.isNotEmpty() && pricePerDay.isNotEmpty() && selectedCategoryId.isNotEmpty()) {
                    val bookingItem = BookingItem(
                        id = bookingId,
                        categoryId = selectedCategoryId,
                        pricePerDay = pricePerDay,
                        name = name,
                        info = info,
                        quantity = quantity,
                        createdBy = userInfo?.id ?: ""
                    )

                    adminBookingViewModel?.addBookingItem(
                        selectedCategory = selectedCategoryId,
                        bookingItem = bookingItem,
                    )
                    name = ""
                    info = ""
                    pricePerDay = ""
                    selectedCategoryName = ""
                    selectedCategoryId = ""
                    quantity = ""
                }
            },
            onAddExtrasClick = {
                adminBookingViewModel?.setSelectedCategory(selectedCategoryId)
                adminBookingViewModel?.setItems(name = name, info = info, price = pricePerDay, quantity = quantity)
                navigateToExtra(selectedBookingId)
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
        userInfo = CompanyProfileModel(
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