package com.basecampers.basecamp.tabs.booking.admin.createBooking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyProfileModel
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.components.CategoriesCard
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories

@Composable
fun AdminCategoriesView(
    adminBookingViewModel: AdminBookingViewModel,
    goBack: () -> Unit,
    navigateToBooking: (String) -> Unit,
    navigateOverview: () -> Unit
) {

    val companyId = UserSession.companyProfile.value?.companyId ?: ""

    var category by remember { mutableStateOf("") }
    val categories by adminBookingViewModel.categories.collectAsState()
    val bookingItems by adminBookingViewModel.bookingItems.collectAsState()

    var info by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isAddVisible by remember { mutableStateOf(false) }

    
    
    Column(Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(Modifier.weight(1f)) {
        CustomColumn(
            title = "Categories",
            onClick = { isAddVisible = !isAddVisible },
            )
        {

            Column(Modifier.padding(start = 16.dp)) {
                if (categories.isNotEmpty()) {
                    categories.forEach { category ->
                        val categoryItems = bookingItems.filter { it.categoryId == category.id }
                        CategoriesCard(
                            onClick = {
                                adminBookingViewModel.updateCategoriesValues(
                                    id = category.id,
                                    name = category.name,
                                    info = category.info,
                                    createdBy = category.createdBy
                                )
                                navigateToBooking(category.id)
                            },
                            title = category.name,
                            info = category.info,
                            itemList = categoryItems,
                            modifier = Modifier
                        )
                    }
                } else {
                    Text(text = "No categories found")
                }
            }

        }
            Spacer(Modifier.height(8.dp))
            if (isAddVisible) {
                CustomColumn {
                    OutlinedTextField(
                        label = { Text("Category") },
                        value = category,
                        onValueChange = { newCategory ->
                            val filteredValue = newCategory.replace("\\s".toRegex(), "")
                            category = filteredValue
                        },
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        label = { Text("Info") },
                        value = info,
                        onValueChange = { newInfo ->
                            val filteredValue = newInfo.replace("\\s".toRegex(), "")
                            info = filteredValue
                        },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        CustomButton(onClick = {
                            if (category.isNotEmpty()) {
                                val categoryId =
                                    "${System.currentTimeMillis()}_${(1000..9999).random()}"

                                companyId.let { user ->
                                    adminBookingViewModel.addBookingCategory(
                                        bookingCategory = BookingCategories(
                                            id = categoryId,
                                            name = category,
                                            info = info,
                                            createdBy = companyId.toString()
                                        )
                                    )
                                }
                                category = ""
                                info = ""
                                error = ""
                            } else {
                                error = "Category name is required"
                            }
                        }, text = "Save")
                        CustomButton(onClick = {
                            category = ""
                            info = ""
                            error = ""
                            isAddVisible = !isAddVisible
                                               },
                            text = "Cancel")
                    }
                }
            }
        }
        Row() {
            CustomButton(text = "Back", onClick = goBack)
            Spacer(modifier = Modifier.weight(1f))
            CustomButton(text = "Add category", onClick = { isAddVisible = !isAddVisible })
            CustomButton(text = "Overview", onClick = navigateOverview)



        }
    }
}


@Preview(showBackground = true)
@Composable
fun CategoriesViewPreview() {
    AdminCategoriesView(
        adminBookingViewModel = viewModel(),
        goBack = {},
        navigateToBooking = {},
        navigateOverview = {}
    )
    
}