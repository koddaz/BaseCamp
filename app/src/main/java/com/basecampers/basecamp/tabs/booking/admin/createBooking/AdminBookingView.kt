package com.basecampers.basecamp.tabs.booking.admin.createBooking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.home.QuickActionButton
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingView(
    modifier: Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel,
    goBack: () -> Unit,
    navigateToExtra: () -> Unit = {},
    navigateHome: () -> Unit = {}
) {
    val userId = UserSession.userId
    var expanded by remember { mutableStateOf(false) }

    val categories by adminBookingViewModel.categories.collectAsState()
    val bookingId = "${System.currentTimeMillis()}_${(1000..9999).random()}"
    val selectedItem by adminBookingViewModel.selectedItem.collectAsState()
    val selectedCategory by adminBookingViewModel.selectedCategory.collectAsState()

    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SecondaryAqua.copy(alpha = 0.2f),
                        AppBackground
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Text(
                text = "Add an Item",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Column(Modifier.weight(1f).verticalScroll(scrollState)) {

                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    CategoryDropdown(
                        categories = categories,
                        expanded = expanded,
                        selectedCategoryName = selectedCategory?.name ?: "",
                        onExpandedChange = { expanded = !expanded },
                        onCategorySelected = { category ->

                            adminBookingViewModel.updateCategoriesValues(
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

                BookingItemForm(
                    name = name,
                    info = info,
                    navigateHome = navigateHome,
                    navigateToExtra = navigateToExtra,
                    pricePerDay = pricePerDay,
                    quantity = quantity,
                    onNameChange = { name = it },
                    onInfoChange = { if (it.length <= 250) info = it },
                    onPricePerDayChange = { pricePerDay = it },
                    onQuantityChange = { quantity = it },
                    onAddItemClick = {
                        if (name.isNotEmpty() && pricePerDay.isNotEmpty() && selectedCategory != null) {

                            adminBookingViewModel.updateBookingItemValues(
                                id = bookingId,
                                name = name,
                                info = info,
                                price = pricePerDay,
                                quantity = quantity,
                            )

                            adminBookingViewModel.addBookingItem(
                                selectedCategory = selectedCategory?.id ?: "",
                                bookingItem = BookingItem(
                                    id = bookingId,
                                    name = name,
                                    info = info,
                                    pricePerDay = pricePerDay,
                                    quantity = quantity,
                                    categoryId = selectedCategory?.id ?: "",
                                    createdBy = userId.value.toString()
                                ),
                            )
                            name = ""
                            info = ""
                            pricePerDay = ""
                            quantity = ""
                        }
                    },
                    onAddExtrasClick = {
                        adminBookingViewModel.updateBookingItemValues(
                            id = bookingId,
                            name = name,
                            info = info,
                            price = pricePerDay,
                            quantity = quantity,
                        )
                        // Use bookingId instead of selectedItem?.id
                        adminBookingViewModel.retrieveBookingExtras(selectedCategory?.id ?: "", bookingId)
                        navigateToExtra()
                    }
                )
            }

        }
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
        modifier = Modifier.fillMaxWidth().background(AppBackground),
    ) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a category") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    focusedLabelColor = SecondaryAqua
                ),
                shape = RoundedCornerShape(12.dp)
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
                            .clickable { onCategorySelected(category) }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingItemForm(
    navigateHome: () -> Unit,
    navigateToExtra: () -> Unit,
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
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                label = { Text("Name") },
                value = name,
                onValueChange = onNameChange,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    focusedLabelColor = SecondaryAqua
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                label = { Text("Info") },
                maxLines = 5,
                minLines = 5,
                value = info,
                onValueChange = onInfoChange,
                supportingText = { Text("${info.length}/250 characters") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    focusedLabelColor = SecondaryAqua
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                label = { Text("Price per day") },
                value = pricePerDay,
                onValueChange = onPricePerDayChange,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    focusedLabelColor = SecondaryAqua
                ),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                label = { Text("Quantity") },
                value = quantity,
                onValueChange = onQuantityChange,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAqua,
                    focusedLabelColor = SecondaryAqua
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
    Spacer(Modifier.height(8.dp))
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(Modifier.weight(1f))
            QuickActionButton(
                icon = Icons.Default.Save,
                text = "Save",
                onClick = {
                    onAddItemClick()
                    navigateHome()
                          },  // Call the function
                gradientColors = listOf(SecondaryAqua, SecondaryAqua.copy(alpha = 0.7f))
            )

            QuickActionButton(
                icon = Icons.Default.Add,
                text = "Add Extra",
                onClick = {
                    onAddExtrasClick()
                },
                gradientColors = listOf(SecondaryAqua, SecondaryAqua.copy(alpha = 0.7f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminBookingViewPreview() {
    AdminBookingView(
        adminBookingViewModel = viewModel(),
        goBack = {},
    )
}