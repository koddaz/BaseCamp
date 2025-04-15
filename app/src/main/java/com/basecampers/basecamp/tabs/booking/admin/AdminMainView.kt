package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.components.CustomColumn
import com.basecampers.basecamp.tabs.booking.models.AdminBookingViewModel

@Composable
fun AdminMainView(
    modifier: Modifier = Modifier,
    adminBookingViewModel: AdminBookingViewModel,
    navigateCat: () -> Unit,
    navigateBooking: () -> Unit,
    navigateExtra: () -> Unit,
    userInfo: CompanyProfileModel?
) {
    val categories by adminBookingViewModel.categories.collectAsState()
    val categoryItems by adminBookingViewModel.categoryItems.collectAsState()
    
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier.weight(1f)) {
            if (categories.isNotEmpty()) {
                categories.forEach { category ->
                    // Fetch items for this category
                    LaunchedEffect(category.id) {
                        adminBookingViewModel.retrieveBookingItems(category.id)
                    }
                    
                    CustomColumn(
                        title = category.name,
                    ) {
                        Text(
                            text = category.info,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Display items for this specific category
                        val items = categoryItems[category.id] ?: emptyList()
                        
                        if (items.isNotEmpty()) {
                            Text(
                                text = "Items:",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            
                            items.forEach { item ->
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(text = item.name, fontWeight = FontWeight.Bold)
                                        Text(text = item.info)
                                        Text(text = "Price: ${item.pricePerDay} per day")
                                        Text(text = "Quantity: ${item.quantity}")
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        } else {
                            Text(text = "No items in this category")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Row() {
            CustomButton(
                onClick = {navigateCat()},
                text = "Add Category"
            )
            CustomButton(
                onClick = {navigateBooking()},
                text = "Add Item"
            )
            CustomButton(
                onClick = {navigateExtra()},
                text = "Add Extra"
            )
            
        }
    }
    
    
    
}