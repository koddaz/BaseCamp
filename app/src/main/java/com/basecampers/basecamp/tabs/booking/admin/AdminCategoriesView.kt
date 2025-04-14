package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.UserModel
import com.basecampers.basecamp.UserStatus
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.BookingItem

@Composable
fun AdminCategoriesView(
    authViewModel: AuthViewModel?,
    userInfo: UserModel?,
    modifier : Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel? = viewModel(),
    goBack: () -> Unit,
    navigateToBooking: (String) -> Unit
) {
    
    var category by remember { mutableStateOf("") }
    val categories by adminBookingViewModel?.categories?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    val bookingItems by adminBookingViewModel?.bookingItems?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    var info by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isAddVisible by remember { mutableStateOf(false) }
    
    
    Column(modifier.fillMaxSize().padding(16.dp)) {
        CustomButton(text = "Back", onClick = goBack)
        CustomColumn(
            title = "Categories",
            onClick = { isAddVisible = !isAddVisible },
            imageVector = Icons.AutoMirrored.Filled.NoteAdd)
        {
            
            Column(modifier.padding(start = 16.dp)) {
                if (categories.isNotEmpty()) {
                    categories.forEach { category ->
                        val categoryItems = bookingItems.filter { it.categoryId == category.id }
                        CategoriesCard(
                            onClick = {
                                adminBookingViewModel?.setSelectedCategory(category.id)
                                navigateToBooking(category.id)
                            },
                            title = category.name,
                            info = category.info,
                            itemList = categoryItems,
                            modifier = modifier
                        )
                    }
                } else {
                    Text(text = "No categories found")
                }
            }
            
            
            if (isAddVisible) {
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
                        val createdBy = authViewModel?.getCurrentUserUid() ?: ""
                        
                        
                        userInfo?.let { user ->
                            adminBookingViewModel?.addBookingCategory(
                                bookingCategory = BookingCategories(
                                    id = categoryId,
                                    name = category,
                                    info = info,
                                    createdBy = createdBy
                                )
                            )
                        }
                        
                        
                        // Reset fields after successful add
                        category = ""
                        info = ""
                        error = ""
                    } else {
                        error = "Category name is required"
                    }
                }, text = "Add category")
            }
        }
    }
}

@Composable
fun CategoriesCard(
    modifier: Modifier = Modifier,
    title: String = "",
    info: String = "",
    itemList: List<BookingItem>? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = { onClick() })
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(text = title)
            Text(text = info)
            
            if (itemList != null) {
                Text("Items in this category:", style = typography.labelLarge)
                itemList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${item.name} - ${item.pricePerDay}",
                            style = typography.bodyMedium
                        )
                    }
                }
            } else {
                Text("No items in this category", style = typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesViewPreview() {
    AdminCategoriesView(
        adminBookingViewModel = null,
        authViewModel = null,
        userInfo = UserModel(
            name = "asd",
            email = "asd",
            status = UserStatus.ADMIN,
            imageUrl = null,
            id = "122",
        ),
        goBack = {},
        navigateToBooking = {}
    )
    
}