package com.basecampers.basecamp.tabs.profile.navHost

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.tabs.booking.admin.AdminNavHost
import com.basecampers.basecamp.tabs.profile.screens.EditProfileScreen
import com.basecampers.basecamp.tabs.profile.screens.OptionsScreen
import com.basecampers.basecamp.tabs.profile.screens.ProfileScreen
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@Composable
fun ProfileNavHost(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    companyViewModel: CompanyViewModel,
    selectedProfileTabIndex: Int = 0,
    onProfileTabSelected: (Int) -> Unit = {}
) {
    var currentProfileTabIndex by remember { mutableIntStateOf(selectedProfileTabIndex) }
    
    LaunchedEffect(selectedProfileTabIndex) {
        currentProfileTabIndex = selectedProfileTabIndex
    }
    
    when (currentProfileTabIndex) {
        0 -> ProfileScreen(
            onNavigateToAdmin = { onProfileTabSelected(2) },
            onNavigateToOptions = { onProfileTabSelected(3) }
        )
        1 -> EditProfileScreen(
            profileViewModel = profileViewModel,
            onNavigateBack = { onProfileTabSelected(0) }
        )
        2 -> AdminNavHost(
            onNavigateBack = { onProfileTabSelected(0) }
        )
        3 -> OptionsScreen(
            onNavigateToEdit = { onProfileTabSelected(1) },
            onNavigateBack = { onProfileTabSelected(0) },
            authViewModel = authViewModel,
            companyViewModel = companyViewModel
        )
        else -> Text("Error: Profile tab not found")
    }
}