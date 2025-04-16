package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.BookingCard
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.tabs.booking.models.BookingItem
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import kotlinx.coroutines.selects.select
import kotlin.String


@Composable
fun UserCategoryView(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit
) {
    val categoryList by bookingViewModel?.categoriesList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        categoryList.forEach { category ->
            CategoriesCard(
                title = category.name,
                info = category.info,
                onClick = {
                    bookingViewModel?.setSelectedCategory(category)
                    navBooking(category.id)
                }
            )
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
    val selectedItem by bookingViewModel?.selectedBookingItem?.collectAsState() ?: remember { mutableStateOf<BookingItem?>(null) }
    val itemList by bookingViewModel?.bookingItemsList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            itemList.forEach { item ->
                BookingCard(
                    selected = item.id == selectedCategory?.id,
                    title = item.name,
                    info = item.info,
                    price = item.pricePerDay,
                    onClick = {
                        bookingViewModel?.setSelection(item.id, item)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {

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
            CustomButton(text = "Next", onClick = {
                selectedItem?.let { item ->
                    bookingViewModel?.setSelection(item.id, item)
                    bookingViewModel?.retrieveExtraItems(item.categoryId, item.id)
                    navExtra(item.id)
                }

            })
        }
    }

}

@Composable
fun UserExtraItem(
    selectedItem: BookingItem?,
    bookingViewModel: UserBookingViewModel?,
    navBooking: () -> Unit,
) {

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
        CustomButton(text = "Next", onClick = {
            navBooking()
        })
    }
}


@Composable
fun UserConfirmationView(
    selectedItem: BookingItem?,
    formattedDateRange: String,
    selectedExtraItems: List<BookingExtra?>,
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {
        CustomColumn() {
            BookingCard(
                title = selectedItem?.name ?: "",
                info = selectedItem?.info ?: "",
                price = "",
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Date Range: $formattedDateRange")

            Spacer(modifier = Modifier.height(8.dp))


            selectedExtraItems.forEachIndexed { index, extra ->
                Card() {
                    Column() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {

                    Column() {
                        Text(text = "Selected Extra: ${extra?.name}")
                        Text(text = "Price: ${extra?.price}")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable {
                            extra?.id?.let { id ->
                                bookingViewModel?.removeExtraItem(id)
                            }
                        })
                }
                        }
            }
            }
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