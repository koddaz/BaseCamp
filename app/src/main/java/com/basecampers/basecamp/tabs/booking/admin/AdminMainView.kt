package com.basecampers.basecamp.tabs.booking.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.components.CustomButton
import com.basecampers.basecamp.tabs.booking.admin.viewModel.AdminBookingViewModel

@Composable
fun AdminMainView(
    modifier: Modifier = Modifier,
    navigateCat: () -> Unit,
    navigateBooking: () -> Unit,
    navigateOverview: () -> Unit,
    onNavigateBack: () -> Unit,
) {

    val companyProfile = UserSession.companyProfile.collectAsState()
    
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier.weight(1f)) {
            Text("Here you navigate to the different tabs")
            CustomButton(
                text = "Add a category",
                onClick = {
                    navigateCat()
                }
            )
            CustomButton(
                text = "Add a booking item",
                onClick = {
                    navigateBooking()
                }
            )
            CustomButton(
                text = "Overview",
                onClick = {
                    navigateOverview()
                }
            )
            CustomButton(
                text = "Back",
                onClick = {
                    onNavigateBack()
                }
            )

        }

    }
    
    
    
}