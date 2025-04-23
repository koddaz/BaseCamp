package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.components.HorizontalOptionCard
import com.basecampers.basecamp.ui.theme.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Add Section
        Text(
            text = "Add New",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Categories Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = navigateCat)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Category,
                        contentDescription = "Categories",
                        tint = SecondaryAqua,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                }
            }

            // Booking Items Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = navigateBooking)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Booking Items",
                        tint = SecondaryAqua,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Booking Items",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Current Section
        Text(
            text = "Current",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = navigateOverview)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Overview",
                    tint = SecondaryAqua,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Bookings Overview",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "View and manage current bookings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Back Button
        Button(
            onClick = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryAqua.copy(alpha = 0.1f),
                contentColor = SecondaryAqua
            ),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back to Profile")
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