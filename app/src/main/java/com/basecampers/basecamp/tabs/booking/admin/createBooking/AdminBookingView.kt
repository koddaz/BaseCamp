package com.basecampers.basecamp.tabs.booking.admin.createBooking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingView(
    authViewModel: AuthViewModel? = viewModel(),
    modifier: Modifier = Modifier,
    userInfo: CompanyProfileModel?,
    adminBookingViewModel: AdminBookingViewModel?,
    goBack: () -> Unit,
    navigateToExtra: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    var userId = authViewModel?.getCurrentUserUid()
    val categories by adminBookingViewModel?.categories?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val bookingId = "${System.currentTimeMillis()}_${(1000..9999).random()}"

    val selectedItem by adminBookingViewModel?.selectedItem?.collectAsState() ?: remember { mutableStateOf(
        BookingItem(
            id = "",
            name = "",
            info = "",
            pricePerDay = "",
            quantity = "",
            categoryId = "",
            createdBy = ""
        )) }
    val selectedCategory by adminBookingViewModel?.selectedCategory?.collectAsState() ?: remember { mutableStateOf(
        BookingCategories(
            id = "",
            name = "",
            info = "",
            createdBy = ""
        )) }

    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }



    Column(modifier.fillMaxSize()) {
        Column(modifier.weight(1f)) {
            CustomColumn(title = "Add Booking Item") {
            CategoryDropdown(
                categories = categories,
                expanded = expanded,
                selectedCategoryName = selectedCategory?.name ?: "",
                onExpandedChange = { expanded = !expanded },
                onCategorySelected = { category ->

                    adminBookingViewModel?.updateCategoriesValues(
                        id = category.id,
                        name = category.name,
                        info = category.info,
                        createdBy = category.createdBy

                    )
                    expanded = false
                }
            )
        }
            Spacer(modifier.height(8.dp))
            Column() {
                BookingItemForm(
                    name = name,
                    info = info,
                    pricePerDay = pricePerDay,
                    quantity = quantity,
                    onNameChange = { name = it },
                    onInfoChange = { if (it.length <= 500) info = it },
                    onPricePerDayChange = { pricePerDay = it },
                    onQuantityChange = { quantity = it },

                    onAddItemClick = {
                        if (name.isNotEmpty() && pricePerDay.isNotEmpty() && selectedCategory != null) {

                            adminBookingViewModel?.updateBookingItemValues(
                                id = bookingId,
                                name = name,
                                info = info,
                                price = pricePerDay,
                                quantity = quantity,
                            )

                            adminBookingViewModel?.addBookingItem(
                                selectedCategory = selectedCategory?.id ?: "",
                                bookingItem = BookingItem(
                                    id = bookingId,
                                    name = name,
                                    info = info,
                                    pricePerDay = pricePerDay,
                                    quantity = quantity,
                                    categoryId = selectedCategory?.id ?: "",
                                    createdBy = userId ?: ""
                                ),
                            )
                            name = ""
                            info = ""
                            pricePerDay = ""
                            quantity = ""
                        }
                    },
                    onAddExtrasClick = {
                        adminBookingViewModel?.updateBookingItemValues(
                            id = bookingId,
                            name = name,
                            info = info,
                            price = pricePerDay,
                            quantity = quantity,
                        )
                        navigateToExtra(selectedItem?.id ?: "")
                    }
                )
            }

    }
        CustomButton(text = "Back", onClick = goBack)
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
    CustomColumn {
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
    }
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Spacer(Modifier.weight(1f))
        CustomButton(onClick = onAddItemClick, text = "Save")
        CustomButton(text = "Add Extras", onClick = onAddExtrasClick)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBookingViewPreview() {
    AdminBookingView(
        userInfo = CompanyProfileModel(
            imageUrl = null,
            bio = "",
            status = UserStatus.ADMIN,
            id = "",
        ),
        adminBookingViewModel = null,
        goBack = {},
    )


}