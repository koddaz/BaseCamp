package com.basecampers.basecamp.tabs.booking.admin.createBooking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextSecondary

@Composable
fun AdminCategoriesView(
    adminBookingViewModel: AdminBookingViewModel,
    goBack: () -> Unit,
    navigateToBooking: (String) -> Unit,
    navigateOverview: () -> Unit
) {

    val scrollState = rememberScrollState()
    val companyId = UserSession.companyProfile.value?.companyId ?: ""

    var category by remember { mutableStateOf("") }
    val categories by adminBookingViewModel.categories.collectAsState()
    val bookingItems by adminBookingViewModel.bookingItems.collectAsState()

    var info by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isAddVisible by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SecondaryAqua.copy(alpha = 0.2f),
                        AppBackground
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Header
            Text(
                text = "Bookings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Column(Modifier.weight(1f).verticalScroll(scrollState)) {


                Column(Modifier.padding(start = 16.dp)) {
                    if (categories.isNotEmpty()) {
                        categories.forEach { category ->
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
                                modifier = Modifier
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    } else {
                        Text(text = "No categories found")
                    }
                }
                Spacer(Modifier.height(8.dp))

                if (isAddVisible) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {
                                category = it
                            },
                            label = { Text("Category", style = MaterialTheme.typography.bodyLarge) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SecondaryAqua,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        OutlinedTextField(
                            value = info,
                            onValueChange = {
                                info = it
                            },
                            label = { Text("Info", style = MaterialTheme.typography.bodyLarge) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SecondaryAqua,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                    CustomButton(onClick = {
                        if (category.isNotEmpty()) {
                            val categoryId = "${System.currentTimeMillis()}_${(1000..9999).random()}"

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
                }




                Spacer(Modifier.weight(1f))

                Column(modifier = Modifier.fillMaxWidth()) {
                    FloatingActionButton(
                        onClick = { isAddVisible = !isAddVisible },
                        modifier = Modifier.padding(16.dp).align(Alignment.End),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Category")
                    }
                }

            }
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