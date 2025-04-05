package com.example.basecamp.tabs.booking.admin

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
import com.example.basecamp.UserModel
import com.example.basecamp.components.CustomButton
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
import com.example.basecamp.tabs.booking.models.BookingCategories
import com.example.basecamp.tabs.booking.models.BookingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingView(
    modifier: Modifier = Modifier,
    userInfo: UserModel?,
    authViewModel: AuthViewModel,
    adminBookingViewModel: AdminBookingViewModel,
    goBack: () -> Unit,
    categoryId: String = "",
    navigateToExtra: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }

    val categories by adminBookingViewModel.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var info by remember { mutableStateOf("") }
    var pricePerDay by remember { mutableStateOf("") }

    LaunchedEffect(categoryId) {
        val category = categories.find { it.id == categoryId }
        selectedCategoryName = category?.name ?: ""
    }


    CustomColumn(title = "Add Booking Item") {
        CustomButton(text = "Back", onClick = goBack)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategoryName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a category") },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                modifier = modifier.fillMaxWidth(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategoryName = category.name
                                selectedCategoryId = category.id // Store the ID
                                expanded = false
                            }
                            .padding(16.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Name") },
            value = name,
            onValueChange = { name = it }, // Fixed: Call the function with the argument
            maxLines = 1
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Info") },
            maxLines = 10,
            minLines = 10,
            value = info,
            onValueChange = { if (it.length <= 500) info = it }, // Fixed: Call with argument
            supportingText = { Text("${info.length}/500 characters") }
        )

        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = { Text("Price per day") },
            value = pricePerDay,
            onValueChange = { pricePerDay = it }, // Fixed: Call with argument
            maxLines = 1,
        )


        CustomButton(onClick = {
            val price = pricePerDay.toDoubleOrNull() ?: 0.0
            val bookingId = "${System.currentTimeMillis()}_${(1000..9999).random()}"
            adminBookingViewModel.addBookingItem(
                bookingItem = BookingItem(
                    id = bookingId,
                    pricePerDay = price,
                    name = name,
                    info = info,
                    createdBy = userInfo?.name?: ""
                ),
                selectedCategory = selectedCategoryId,
            )
            name = ""
            info = ""
            pricePerDay = ""
            selectedCategoryName = ""
            selectedCategoryId = ""
        }, text = "Add item")
        CustomButton(text = "Add Extras", onClick = { navigateToExtra(selectedCategoryId, name, info, pricePerDay) })
    }
}