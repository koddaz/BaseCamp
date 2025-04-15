package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel

@Composable
fun SelectBookingView(
    categoryList: List<BookingCategories>,
    itemList: List<BookingItem>,
    bookingViewModel: UserBookingViewModel?,
    navExtra: () -> Unit,
) {
    var selectedCategoryId by remember { mutableStateOf("") }
    val selectedItemId by bookingViewModel?.selectedItemId?.collectAsState() ?: remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    val formattedDateRange by bookingViewModel?.formattedDateRange?.collectAsState() ?: remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(modifier = Modifier.weight(1f)) {
            CustomColumn(title = "Select a category") {
                LazyRow {
                    items(categoryList.size) { index ->
                        val category = categoryList[index]

                        CategoriesCard(title = category.name, info = category.info, onClick = {
                            selectedCategoryId = category.id
                        })
                    }
                }

            }
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
                                bookingViewModel?.setSelection(item.id)
                            }
                        )
                    }
                }
            }
        }
        CustomColumn() {
            CustomButton(text = "Next", onClick = {

                val selectedItem = itemList.find { it.id == selectedItemId }
                selectedItem?.let { item ->
                    bookingViewModel?.setSelection(item.id, item)
                    bookingViewModel?.retrieveExtraItems(item.categoryId, item.id)
                    navExtra()

                }

            })
        }


    }
}