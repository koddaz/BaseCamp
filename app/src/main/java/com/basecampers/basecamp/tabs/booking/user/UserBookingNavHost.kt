package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.navigation.models.AuthViewModel
import com.basecampers.basecamp.tabs.booking.admin.CategoriesCard
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.BookingItems

import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel

@Composable
fun UserBookingNavHost(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val bookingViewModel: UserBookingViewModel = viewModel()

    val categoryList by bookingViewModel.categories.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val itemList: List<BookingItem> by bookingViewModel.bookingItemsList.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            bookingViewModel.setUser(user)
        }
    }

    // Fix the category/item loading
    LaunchedEffect(Unit) {
        bookingViewModel.retrieveCategories()
    }

    // Separate LaunchedEffect to react when categories change
    LaunchedEffect(categoryList) {
        if (categoryList.isNotEmpty()) {
            categoryList.forEach { category ->
                bookingViewModel.retrieveBookingItems(category.id)
            }
        }
    }



    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            BookingView(onClick = { navController.navigate("selectCategory") })
        }
        composable("selectCategory") { SelectCategoryView(categoryList = categoryList, itemList = itemList, bookingViewModel = bookingViewModel) }
    }
}
@Composable
fun BookingView(onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomButton(text = "Book an item", onClick = onClick )
    }
}

@Composable
fun SelectCategoryView(
    categoryList: List<BookingCategories>,
    itemList: List<BookingItem>,
    bookingViewModel: UserBookingViewModel?
) {
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedItemId by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    val formattedDateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    val amountOfDays by bookingViewModel?.amountOfDays?.collectAsState() ?: remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        CustomColumn(title = "Select a category") {
            LazyRow {
                items(categoryList.size) { index ->
                    val category = categoryList[index]

                    CategoriesCard(title = category.name, info = category.info, onClick = {
                        selectedCategoryId = category.id
                    })
                    //Text(text = category.name)

                    /*
                    itemList.filter { it.categoryId == category.id }.forEach { item ->
                        Text(text = "• ${item.name} - €${item.pricePerDay}/day")
                    }

                     */
                }
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        SelectExtraView(
            selectedItem = selectedItemId,
            totalDays = amountOfDays.toString(),
            totalPrice = "FREE!"
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomColumn() {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formattedDateRange,
                onValueChange = {},
                label = { Text("Date Range") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Range",
                        modifier = Modifier.clickable {
                            showDatePicker = !showDatePicker
                        }
                    )
                },
            )
            if (showDatePicker) {
                DatePickerView(
                    startDate = startDate,
                    endDate = endDate,
                    onDateRangeSelected = { startDate, endDate ->
                        bookingViewModel?.updateSelectedDateRange(startDate, endDate)
                    },
                    onDismiss = {
                        showDatePicker = !showDatePicker
                    }


                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        CustomColumn(title = "Select an item") {

            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                val filteredItems = itemList.filter { it.categoryId == selectedCategoryId }

                items(filteredItems.size) { index ->
                    val item = filteredItems[index]
                    BookingCard(
                        selected = item.id == selectedItemId,
                        title = item.name,
                        info = item.info,
                        price = item.pricePerDay,
                        modifier = Modifier.fillParentMaxWidth(),
                        onClick = {
                            selectedItemId = item.id

                        }
                    )
                }
            }
        }
        CustomColumn() {
            CustomButton(text = "Next", onClick = {

                val selectedItem = itemList.find { it.id == selectedItemId }
                // Only call setSelection if we found the item
                selectedItem?.let { item ->
                    bookingViewModel?.setSelection(item, startDate, endDate )
                }

            })
        }


    }
}

@Composable
fun SelectExtraView(
    selectedItem: String,
    totalDays: String,
    totalPrice: String) {

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Select extra items")
        Text(text = selectedItem)
        Text(text = totalDays)
        Text(text = totalPrice)

        
    }
}

@Composable
fun BookingCard(
    modifier: Modifier = Modifier,
    title: String,
    info: String,
    price: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .background(color = if (selected) Color.Green else Color.White)
            .clickable(onClick = onClick)
    ) {

            Text(
                text = title,
                style = typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
            Column(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                Text("Place for picture",
                    style = typography.titleMedium,
                    modifier = Modifier.padding(16.dp))
            }
            Row() {
                Text(
                    text = info,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = price,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
}

@Preview
@Composable
fun BookingViewPreview() {
    // Sample categories
    val dummyCategories = listOf(
        BookingCategories(
            id = "cat1",
            name = "Equipment",
            info = "Professional equipment for rent",
            createdBy = "admin"
        ),
        BookingCategories(
            id = "cat2",
            name = "Vehicles",
            info = "Cars and trucks available",
            createdBy = "admin"
        ),
        BookingCategories(
            id = "cat3",
            name = "Tools",
            info = "Hand and power tools",
            createdBy = "admin"
        )
    )

    // Sample items linked to categories
    val dummyItems = listOf(
        BookingItem(
            id = "item1",
            categoryId = "cat1",
            pricePerDay = "24.99",
            name = "Professional Camera",
            info = "High resolution DSLR camera",
            quantity = "3"
        ),
        BookingItem(
            id = "item2",
            categoryId = "cat1",
            pricePerDay = "15.99",
            name = "Drone",
            info = "4K recording capability",
            quantity = "2"
        ),
        BookingItem(
            id = "item3",
            categoryId = "cat2",
            pricePerDay = "99.99",
            name = "SUV",
            info = "All terrain vehicle with AC",
            quantity = "1"
        ),
        BookingItem(
            id = "item4",
            categoryId = "cat3",
            pricePerDay = "12.50",
            name = "Power Drill",
            info = "Cordless with extra batteries",
            quantity = "5"
        )
    )

    SelectCategoryView(
        categoryList = dummyCategories,
        itemList = dummyItems,
        bookingViewModel = null,
    )
}