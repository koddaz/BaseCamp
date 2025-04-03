package com.example.basecamp.tabs.booking.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.UserModel
import com.example.basecamp.UserStatus
import com.example.basecamp.components.CustomButton
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBooking(
    user: UserModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel = viewModel(),
    onClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val categories by adminBookingViewModel.categories.collectAsState()

    val scrollState = rememberScrollState()


    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState)) {

        var pageNumber by remember { mutableStateOf(1) }

        Row(modifier.fillMaxWidth()) {
            CustomButton(text = "USER", onClick = onClick)
            CustomButton(text = "change page", onClick = {
                pageNumber = if (pageNumber == 1) {
                    2
                } else {
                    1
                }
            })
        }

        if(pageNumber == 1) {
            CategoriesView(
                user = user,
                authViewModel = authViewModel,
                categories = categories,
                adminBookingViewModel = adminBookingViewModel,
                info = info,
                errorMessage = errorMessage,
                onInfoChange = { info = it },
                onErrorChange = { errorMessage = it }
            )
        } else if (pageNumber == 2) {
            AddBookingView(
                user = user,
                authViewModel = authViewModel,
                categories = categories,
                name = name,
                info = info,
                pricePerDay = pricePerDay,
                onNameChange = { name = it },
                onInfoChange = { info = it },
                onPricePerDayChange = { pricePerDay = it }, // Changed to accept String directly
                adminBookingViewModel = adminBookingViewModel
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun AddBookingItemPreview() {
    AdminBooking(authViewModel = viewModel(), user = UserModel(
        email = "",
        name = "",
        imageUrl = null,
        bio = "",
        status = UserStatus.SUPER_USER,
        id = "",
        companyName = "",
    ))
}