

    import androidx.compose.foundation.clickable
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
            import androidx.compose.ui.unit.dp
            import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.basecamp.UserModel
    import com.example.basecamp.components.CustomButton
            import com.example.basecamp.components.CustomColumn
            import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
            import com.example.basecamp.tabs.booking.models.BookingExtra
            import com.example.basecamp.tabs.booking.models.BookingItem

            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun AdminExtrasView(
                categoryId: String = "",
                bookingId: String = "",
                bookingName: String = "",
                bookingInfo: String = "",
                bookingPrice: String = "",
                goBack: () -> Unit,
                adminBookingViewModel: AdminBookingViewModel = viewModel(),
                userInfo: UserModel?) {

                val categories by adminBookingViewModel.categories.collectAsState()

                var expandedCategory by remember { mutableStateOf(false) }
                var selectedCategoryId by remember { mutableStateOf("") }
                var selectedCategoryName by remember { mutableStateOf("") }

                var expandedItem by remember { mutableStateOf(false) }
                var selectedItem by remember { mutableStateOf<BookingItem?>(null) }
                var items by remember { mutableStateOf<List<BookingItem>>(emptyList()) }


                var name by remember { mutableStateOf("") }
                var info by remember { mutableStateOf("") }
                var price by remember { mutableStateOf("") }

                CustomColumn(title = "Add Extra Item") {
                    CustomButton(text = "Back", onClick = goBack)
                    // Category selection

                    Text(text = "Category ID: $categoryId")
                    Text(text = "Booking Name: $bookingName")
                    Text(text = "Booking Info: $bookingInfo")
                    Text(text = "Booking Price: $bookingPrice")

                        /*

                        ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select a category") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            categories.forEach { category ->
                                Text(
                                    text = category.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedCategoryName = category.name
                                            selectedCategoryId = category.id
                                            expandedCategory = false
                                            // Reset item selection when category changes
                                            selectedItem = null
                                            // Here you would fetch items for this category
                                            // adminModel.getItemsByCategory(selectedCategoryId)
                                        }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }

                    // Item selection (only enabled if category is selected)
                    ExposedDropdownMenuBox(
                        expanded = expandedItem && selectedCategoryId.isNotEmpty(),
                        onExpandedChange = { if (selectedCategoryId.isNotEmpty()) expandedItem = !expandedItem },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedItem?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = selectedCategoryId.isNotEmpty(),
                            label = { Text("Select an item") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedItem,
                            onDismissRequest = { expandedItem = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items.forEach { item ->
                                Text(
                                    text = item.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedItem = item
                                            expandedItem = false
                                        }
                                        .padding(16.dp)
                                )
                            }
                        }


                    }
 */
                    // Extra item details
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Name") },
                        value = name,
                        onValueChange = { name = it },
                        maxLines = 1
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Info") },
                        maxLines = 5,
                        minLines = 3,
                        value = info,
                        onValueChange = { if (it.length <= 300) info = it },
                        supportingText = { Text("${info.length}/300 characters") }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Price") },
                        value = price,
                        onValueChange = { price = it },
                        maxLines = 1
                    )

                    CustomButton(
                        onClick = {
                            val selectedItemNotNull = selectedItem
                            if (selectedCategoryId.isNotEmpty() && selectedItemNotNull != null && name.isNotEmpty()) {
                                val priceValue = price.toDoubleOrNull() ?: 0.0
                                val extraItem = BookingExtra(
                                    id = userInfo?.id ?: "", // You might want to generate an ID or get it from elsewhere
                                    price = priceValue,
                                    name = name,
                                    info = info
                                )
                                adminBookingViewModel.addBookingExtra(selectedItemNotNull, extraItem, selectedCategoryId)

                                // Reset form
                                name = ""
                                info = ""
                                price = ""
                            }
                        },
                        text = "Add Extra"
                    )
                }
            }

