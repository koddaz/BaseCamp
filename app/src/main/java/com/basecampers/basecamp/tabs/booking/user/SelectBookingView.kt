package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.components.BookingHistoryCard
import com.basecampers.basecamp.tabs.booking.components.BookingHistoryItem
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun BookingDashboardView(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit
) {
    val categories: List<BookingCategories> by bookingViewModel?.categoriesList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val user by bookingViewModel?.user?.collectAsState() ?: remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<BookingCategories?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(user) {
        if (user != null) {
            isLoading = false
        }
    }

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
                .padding(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Bookings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // New Booking Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "New Booking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedCategory?.name ?: "Select a category",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDropdownExpanded = true },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isDropdownExpanded) 
                                            Icons.Default.ArrowDropUp 
                                        else 
                                            Icons.Default.ArrowDropDown,
                                        contentDescription = "Toggle dropdown",
                                        tint = SecondaryAqua
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryAqua,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                )
                            )

                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardBackground)
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = category.info,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                onClick = {
                                selectedCategory?.let { category ->
                    bookingViewModel?.setSelectedCategory(category)
                    navBooking(category.id)
                }
                            },
                            enabled = selectedCategory != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryAqua,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue Booking")
                        }
                    }
                }
            }

            // My Bookings Section
            item {
                Text(
                    text = "My Bookings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Sample active bookings - replace with actual data
                    items(3) { index ->
                        ActiveBookingCard(
                            title = "Sample Booking ${index + 1}",
                            date = "May ${index + 10}, 2024",
                            status = if (index == 0) "Active" else "Upcoming"
                        )
                    }
                }
            }

            // Booking History Section
            item {
                Text(
                    text = "Booking History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Sample history items - replace with actual data
            items(3) { index ->
                BookingHistoryCard(
                    booking = BookingHistoryItem(
                        id = "booking_$index",
                        itemName = "Sample Booking ${index + 1}",
                        bookingDate = System.currentTimeMillis(),
                        startDate = System.currentTimeMillis(),
                        endDate = System.currentTimeMillis() + (86400000 * 3), // 3 days
                        totalPrice = 150.0 + (index * 50),
                        status = when (index) {
                            0 -> "Confirmed"
                            1 -> "Pending"
                            else -> "Cancelled"
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun ActiveBookingCard(
    title: String,
    date: String,
    status: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (status == "Active") 
                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                else 
                    Color(0xFFFFC107).copy(alpha = 0.1f)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (status == "Active") 
                        Color(0xFF4CAF50)
                    else 
                        Color(0xFFFFC107)
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBookingView(
    bookingViewModel: UserBookingViewModel?,
    onNavigateBack: () -> Unit,
    onNavigateToExtras: (String) -> Unit
) {
    val selectedCategory = bookingViewModel?.selectedCategory?.collectAsState()?.value
    val selectedItem = bookingViewModel?.selectedBookingItem?.collectAsState()?.value
    val filteredItems by bookingViewModel?.bookingItemsList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val dateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
        .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top App Bar with back button
        TopAppBar(
            title = {
                Text(
                    text = selectedCategory?.name ?: "Select Item",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Navigate back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppBackground,
                titleContentColor = TextPrimary
            )
        )

        // Date Selection
        AnimatedVisibility(visible = true) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Select Dates",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    OutlinedTextField(
                        value = dateRange,
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Booking Period") },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select dates",
                                    tint = SecondaryAqua
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = TextPrimary,
                            disabledBorderColor = Color.Gray.copy(alpha = 0.3f),
                            disabledLabelColor = TextSecondary
                        )
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerView(
                startDate = null,
                endDate = null,
                onDateRangeSelected = { start, end ->
                    bookingViewModel?.updateSelectedDateRange(start, end)
                    showDatePicker = false
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }

        // Items List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredItems) { item ->
                        BookingCard(
                            title = item.name,
                            info = item.info,
                            price = item.pricePerDay,
                    selected = item.id == selectedItem?.id,
                            onClick = {
                        bookingViewModel?.setSelection(item.id, item)
                    }
                )
                            }

            if (filteredItems.isEmpty()) {
                item {
                    BasecampCard(
                        title = "No Items Available",
                        subtitle = "No items found in this category",
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Please try another category or check back later",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Bottom Bar with Next Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = AppBackground,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { 
                selectedItem?.let { item ->
                        onNavigateToExtras(item.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                enabled = selectedItem != null && dateRange.isNotEmpty(),
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