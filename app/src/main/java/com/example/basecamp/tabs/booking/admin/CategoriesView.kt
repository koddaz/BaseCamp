package com.example.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.UserModel
import com.example.basecamp.components.CustomButton
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.AdminBookingViewModel
import com.example.basecamp.tabs.booking.models.BookingCategories

@Composable
fun CategoriesView(
    authViewModel: AuthViewModel,
    modifier : Modifier = Modifier,
    categories: List<BookingCategories> = emptyList(),
    adminBookingViewModel: AdminBookingViewModel = viewModel(),
    info: String = "",
    errorMessage: String = "",
    onCategoryChange: (String) -> Unit = {},
    onInfoChange: (String) -> Unit = {},
    onErrorChange: (String) -> Unit = {},
    user: UserModel
) {

    var category by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }


    CustomColumn(title = "Categories") {

        CustomColumn() {
            Column(modifier.padding(start = 16.dp)) {
                if (categories.isNotEmpty()) {
                    categories.forEach { category ->
                        Row {
                            Text(
                                text = "• ",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = category.name)
                        }
                    }
                } else {
                    Text(text = "No categories found")
                }
            }
        }

        OutlinedTextField(
            label = { Text("Category") },
            value = category,
            onValueChange = { newCategory ->
                val filteredValue = newCategory.replace("\\s".toRegex(), "")
                category = filteredValue
            },
            maxLines = 1,
            modifier = modifier.fillMaxWidth()
        )
        CustomButton(onClick = {
            if (category.isNotEmpty()) {
                // Generate a unique ID using current timestamp + random suffix
                val categoryId = "${System.currentTimeMillis()}_${(1000..9999).random()}"
                val createdBy = authViewModel.getCurrentUserUid() ?: ""

                adminBookingViewModel.addBookingCategory(
                    user = user,
                    bookingCategory = BookingCategories(
                        id = categoryId,
                        name = category,
                        info = info,
                        createdBy = createdBy
                    )
                )

                // Reset fields after successful add
                category = ""
                onInfoChange("")
                onErrorChange("")
            } else {
                onErrorChange("Category name is required")
            }
        }, text = "Add category")
    }
}