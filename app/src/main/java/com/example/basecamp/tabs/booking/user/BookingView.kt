package com.example.basecamp.tabs.booking.user

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.basecamp.CompanyModel
import com.example.basecamp.UserModel
import com.example.basecamp.UserStatus
import com.example.basecamp.components.CustomButton
import com.example.basecamp.components.CustomColumn
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.models.BookingCategories
import com.example.basecamp.tabs.booking.models.BookingItems
import com.example.basecamp.tabs.booking.models.UserBookingViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.net.URL
import kotlin.String
import kotlin.text.set
import kotlin.toString

@Composable
fun BookingView(
    modifier: Modifier = Modifier,
    userInfo: UserModel?,
    authViewModel: AuthViewModel,
    bookingViewModel: UserBookingViewModel = viewModel(),
    onClick: () -> Unit
) {
    val categories by bookingViewModel.categories.collectAsState()
    val bookingItems by bookingViewModel.bookingItemsList.collectAsState()
    val selectedDates by bookingViewModel.formattedDateRange.collectAsState()
    val selectedBookingItemFromVM by bookingViewModel.selectedBookingItem.collectAsState()

    var selectedCategoryId by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // Set the user in the ViewModel when available
    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            bookingViewModel.setUser(user)
            bookingViewModel.retrieveCategoriesAndItems()
        }
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            CustomColumn(title = "Category") {
                Row(modifier.fillMaxWidth()) {
                    categories.forEach { category ->
                        CategoryCard(
                            modifier = Modifier.weight(1f),
                            category = category,
                            isSelected = category.id == selectedCategoryId,
                            onClick = {
                                selectedCategoryId = category.id
                                bookingViewModel.retrieveBookingItems(selectedCategoryId)

                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (bookingItems.isNotEmpty()) {
                CustomColumn(title = categories.find { it.id == selectedCategoryId }?.name ?: "") {
                    Column {
                        bookingItems.forEach { item ->
                            ItemCard(
                                item = item,
                                onClick = { bookingViewModel.setSelectedBookingItem(item) },
                                isSelected = selectedBookingItemFromVM?.id == item.id
                            )
                        }
                    }
                }
            } else {
                Text(text = "No items available")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedBookingItemFromVM != null) {
                CustomColumn(title = "Select a date") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        Row(modifier.fillMaxWidth().padding(16.dp)) {
                            Text(text = selectedDates)
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }

                CustomButton(text = "Confirm", onClick = {
                    bookingViewModel.createBooking(onSuccess = {
                        // Handle success
                    }, onFailure = { error ->
                        // Handle error
                    })
                })
            }

            if (showDatePicker) {
                DatePickerView(
                    onDateRangeSelected = { startDate, endDate ->
                        bookingViewModel.updateSelectedDateRange(startDate, endDate)
                    },
                    onDismiss = {
                        showDatePicker = false
                    }
                )
            }
        }

        Column {
            FIREBASETESTSTUFF(authViewModel)
            CustomButton(text = "ADMIN", onClick = onClick)
        }
    }
}

@Composable
fun FIREBASETESTSTUFF(
    authViewModel: AuthViewModel = viewModel()
) {
    val userEmail = "user@example.com"
    val userPassword = "Admin123!"

    val superUserEmail = "super@example.com"
    val superUserPassword = "Admin123!"

    val adminEmail = "admin@example.com"
    val adminPassword = "Admin123!"

    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {


        CustomButton(
            text = "USER",
            onClick = {
               authViewModel.login(userEmail, userPassword)
            }
        )

        CustomButton(
            text = "SUPER",
            onClick = {
                authViewModel.login(superUserEmail, superUserPassword)
            }

        )

        CustomButton(
            text = "ADMIN",
            onClick = {
               authViewModel.login(adminEmail, adminPassword)
            }

        )
        CustomButton(text = "SIGN", onClick = { authViewModel.logout() })
    }

    /*
    CustomButton(text = "CREATE ADMIN ACCOUNT", onClick = {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener

                // Create company document
                val companyName = "Actual Company Name"  // Replace with actual company name or ID
                val companyData =
                    CompanyModel(
                        bio = "No bio yet",
                        imageUrl = null,
                        companyName = "A Company Inc",
                        ownerUID = userId
                    )


                // Create the company in Firestore
                db.collection("companies")
                    .document(companyName)
                    .set(companyData)
                    .addOnSuccessListener {
                        // Create admin user document within the company
                        val userData =
                            UserModel(
                                email = email,
                                name = "Regular User",
                                imageUrl = null,
                                bio = "Company User",
                                status = UserStatus.USER,
                                id = userId,
                                companyName = companyName
                            )


                        // Add the user to the company's users collection
                        db.collection("companies")
                            .document(companyName)
                            .collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("AdminCreation", "Admin user created successfully")
                                // You could show a success message or navigate to a different screen here
                            }
                            .addOnFailureListener { e ->
                                Log.e("AdminCreation", "Error creating admin user", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("AdminCreation", "Error creating company", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("AdminCreation", "Error creating auth user", e)
            }
    })

     */
}


@Composable
fun ItemCard(
    item: BookingItems,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                colorScheme.primaryContainer
            else
                colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(text = item.name,
                color = if (isSelected)
                colorScheme.onPrimaryContainer
            else
                colorScheme.onSurface)
            Text(text = item.info,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)
            Text(text = "$${item.price}",
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface)

        }
    }
}


@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: BookingCategories,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    colorScheme.primaryContainer
                else
                    colorScheme.surface )
    ) {
        Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Text(
                text = category.name,
                color = if (isSelected)
                    colorScheme.onPrimaryContainer
                else
                    colorScheme.onSurface
            )
        }
    }
}