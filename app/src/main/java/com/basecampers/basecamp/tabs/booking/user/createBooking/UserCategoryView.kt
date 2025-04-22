package com.basecampers.basecamp.tabs.booking.user.createBooking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel

@Composable
fun UserCategoryView(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit
) {
    val categoryList by bookingViewModel?.categoriesList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
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