package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.basecampers.basecamp.tabs.booking.models.BookingCategories
import com.basecampers.basecamp.tabs.booking.models.UserBookingViewModel
import com.basecampers.basecamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCategoryView(
    bookingViewModel: UserBookingViewModel?,
    navBooking: (String) -> Unit
) {
    val categories by bookingViewModel?.categoriesList?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    var selectedCategory by remember { mutableStateOf<BookingCategories?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(SecondaryAqua.copy(alpha = 0.1f))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Bookings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // New Booking Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "New Booking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedCategory?.name ?: "Select a category",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDropdownExpanded = true },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (isDropdownExpanded) 
                                            Icons.Default.ArrowDropUp 
                                        else 
                                            Icons.Default.ArrowDropDown,
                                        contentDescription = "Toggle dropdown",
                                        tint = SecondaryAqua
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SecondaryAqua,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                )
                            )

                            DropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(CardBackground)
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = category.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = category.info,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCategory = category
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                selectedCategory?.let { category ->
                                    bookingViewModel?.setSelectedCategory(category)
                                    navBooking(category.id)
                                }
                            },
                            enabled = selectedCategory != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryAqua,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Continue Booking")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: BookingCategories,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (isSelected) 
            SecondaryAqua.copy(alpha = 0.1f) 
        else 
            Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = category.info,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
} 