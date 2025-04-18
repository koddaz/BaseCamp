package com.basecampers.basecamp.tabs.profile.navHost

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.basecampers.basecamp.tabs.profile.screens.EditProfileScreen
import com.basecampers.basecamp.tabs.profile.screens.ProfileScreen
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@Composable
fun ProfileNavHost(
    profileViewModel: ProfileViewModel,
    selectedProfileTabIndex: Int = 0,
    onProfileTabSelected: (Int) -> Unit = {}
) {
    
    var currentProfileTabIndex by remember { mutableIntStateOf(selectedProfileTabIndex) }
    
    LaunchedEffect(selectedProfileTabIndex) {
        currentProfileTabIndex = selectedProfileTabIndex
    }
    
    when (currentProfileTabIndex) {
        0 -> ProfileScreen(
            onNavigateToEdit = { onProfileTabSelected(1) }
        )
        1 -> EditProfileScreen(
            profileViewModel = profileViewModel,
            onNavigateBack = { onProfileTabSelected(0) }
        )
        else -> Text("Error: Profile tab not found")
    }
}