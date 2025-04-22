package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.components.BaseScreenContainer
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.components.BasecampDivider
import com.basecampers.basecamp.components.BasecampOutlinedButton
import com.basecampers.basecamp.components.BasecampTextField
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories

@Composable
fun AdminMainView(
    modifier: Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel,
    navigateCat: () -> Unit,
    navigateBooking: () -> Unit,
    navigateOverview: () -> Unit,
    navigateEdit: () -> Unit = {},
    onNavigateBack: () -> Unit,
) {

    val scrollState = rememberScrollState()


    Column(modifier.fillMaxSize()) {
        BasecampCard(
            title = "Admin Main View",
            subtitle = "Welcome to the admin main view, Here you navigate to the different tabs",
            content = {},
        )
        Column(modifier.weight(1f).verticalScroll(scrollState)) {
            BasecampDivider(text = "Add", thickness = 2f)

            BasecampCard(content = {
                Column() {
                    BasecampOutlinedButton(
                        text = "Categories",
                        onClick = {
                            navigateCat()
                        }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    BasecampOutlinedButton(
                        text = "Booking Items",
                        onClick = {
                            navigateBooking()
                        }
                    )
                }
            })




            BasecampDivider(text = "Current", thickness = 2f)
            BasecampCard(content = {
                BasecampOutlinedButton(
                    text = "Overview",
                    onClick = {
                        navigateOverview()
                    }
                )
            })

            CustomButton(
                text = "Back",
                onClick = {
                    onNavigateBack()
                }
            )
        }
    }
}


@Composable
fun AdminEditItems(adminViewModel: AdminBookingViewModel) {
    val selectedItem = adminViewModel.selectedItem.collectAsState().value

    var name by remember { mutableStateOf(selectedItem?.name ?: "") }
    var info by remember { mutableStateOf(selectedItem?.info ?: "") }
    var price by remember { mutableStateOf(selectedItem?.pricePerDay ?: "") }
    var quantity by remember { mutableStateOf(selectedItem?.quantity ?: "") }


    Column(modifier = Modifier.fillMaxSize()) {
        BasecampCard(title = "Admin Edit Items") {
            Column {
                Text(
                    text = "Welcome to the admin edit items view, Here you navigate to the different tabs",
                    modifier = Modifier.padding(16.dp)
                )
                BasecampTextField(
                    value = name,
                    onValueChange = { newName ->
                        name = newName
                    },
                    label = "Name"
                )
                BasecampTextField(
                    value = info,
                    onValueChange = { newInfo ->
                        info = newInfo
                    },
                    label = "Info"
                )

                BasecampTextField(
                    value = price,
                    onValueChange = { newPrice ->
                        price = newPrice
                    },
                    label = "Info"
                )

                BasecampTextField(
                    value = quantity,
                    onValueChange = { newQuantity ->
                        quantity = newQuantity
                    },
                    label = "Info"
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AdminMainViewPreview() {
    AdminMainView(
        navigateCat = {},
        navigateBooking = {},
        navigateOverview = {},
        onNavigateBack = {},
        adminBookingViewModel = AdminBookingViewModel(
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AdminEditItemsPreview() {
    AdminEditItems(
        adminViewModel = AdminBookingViewModel()
    )
}