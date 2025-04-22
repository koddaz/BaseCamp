package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun UserCategoryView(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit
) {
    val categoryList by bookingViewModel?.categoriesList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(SecondaryAqua.copy(alpha = 0.1f))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Choose Category",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            items(categoryList) { category ->
                CategoriesCard(
                    title = category.name,
                    info = category.info,
                    onClick = {
                        bookingViewModel?.setSelectedCategory(category)
                        navBooking(category.id)
                    }
                )
            }

            if (categoryList.isEmpty()) {
                item {
                    BasecampCard(
                        title = "No Categories",
                        subtitle = "No booking categories available",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Please check back later or contact support",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItemView(
    bookingViewModel: UserBookingViewModel?,
    navExtra: (String) -> Unit,
) {
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    val selectedCategory by bookingViewModel?.selectedCategory?.collectAsState() ?: remember { mutableStateOf<BookingCategories?>(null) }
    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState() ?: remember { mutableStateOf(null) }
    val itemList by bookingViewModel?.bookingItemsList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with category name
            selectedCategory?.let { category ->
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = category.info,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // Items List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(itemList) { item ->
                    BookingCard(
                        selected = item.id == selectedItem?.id,
                        title = item.name,
                        info = item.info,
                        price = item.pricePerDay,
                        onClick = {
                            bookingViewModel?.setSelection(item.id, item)
                        }
                    )
                }

                if (itemList.isEmpty()) {
                    item {
                        BasecampCard(
                            title = "No Items",
                            subtitle = "No items available in this category",
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = "Please check back later or try another category",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Bottom section with date picker and next button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = formattedDateRange,
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Select Dates") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = !showDatePicker }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Dates",
                                tint = SecondaryAqua
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = TextPrimary,
                        disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                        disabledLabelColor = TextSecondary
                    )
                )

                if (showDatePicker) {
                    DatePickerView(
                        startDate = startDate,
                        endDate = endDate,
                        onDateRangeSelected = { start, end ->
                            bookingViewModel?.updateSelectedDateRange(start, end)
                        },
                        onDismiss = {
                            showDatePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedItem?.let { item ->
                            bookingViewModel?.setSelection(item.id, item)
                            bookingViewModel?.retrieveExtraItems(item.categoryId, item.id)
                            navExtra(item.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedItem != null && formattedDateRange.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryAqua,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UserExtraItem(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit,
) {
    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState()
        ?: remember { mutableStateOf<BookingItem?>(null) }
    val extraList by bookingViewModel?.bookingExtraList?.collectAsState()
        ?: remember { mutableStateOf(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        extraList.forEach { extra ->
            BookingCard(
                selected = extra.id == selectedItem?.id,
                title = extra.name,
                info = extra.info,
                price = extra.price,
                onClick = {
                    bookingViewModel?.addExtraItem(extra)
                })
        }
    }
}

@Composable
fun SelectBookingView(
    categoryList: List<BookingCategories>,
    itemList: List<BookingItem>,
    bookingViewModel: UserBookingViewModel?,
    navExtra: () -> Unit,
) {
    val selectedCategory by bookingViewModel?.selectedCategory?.collectAsState()
        ?: remember { mutableStateOf<BookingCategories?>(null) }
    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState()
        ?: remember { mutableStateOf<BookingItem?>(null) }



    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(modifier = Modifier.weight(1f)) {
            CustomColumn(title = "Select a category") {
                LazyRow {
                    items(categoryList.size) { index ->
                        val category = categoryList[index]

                        CategoriesCard(title = category.name, info = category.info, onClick = {
                            bookingViewModel?.setSelectedCategory(category = category)
                        })
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomColumn() {

            }
            Spacer(modifier = Modifier.height(8.dp))
            CustomColumn(title = "Select an item") {

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filteredItems = itemList.filter { it.categoryId == selectedCategory?.id }

                    items(filteredItems.size) { index ->
                        val item = filteredItems[index]
                        BookingCard(
                            selected = item.id == selectedItem?.id,
                            title = item.name,
                            info = item.info,
                            price = item.pricePerDay,
                            modifier = Modifier.fillParentMaxWidth(),
                            onClick = {
                                bookingViewModel?.setSelection(item.id)
                            }
                        )
                    }
                }
            }
        }
        CustomColumn() {
            CustomButton(text = "Next", onClick = {

                val selectedItem = itemList.find { it.id == selectedItem?.id }
                selectedItem?.let { item ->
                    bookingViewModel?.setSelection(item.id, item)
                    bookingViewModel?.retrieveExtraItems(item.categoryId, item.id)
                    navExtra()

                }

            })
        }


    }
}