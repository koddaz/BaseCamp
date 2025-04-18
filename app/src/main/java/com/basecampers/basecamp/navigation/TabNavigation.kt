package com.basecampers.basecamp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.ui.theme.BaseCampTheme
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.tabs.booking.user.UserBookingNavHost
import com.basecampers.basecamp.tabs.home.HomeNavHost
import com.basecampers.basecamp.tabs.profile.navHost.ProfileNavHost
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.tabs.social.navHost.SocialNavHost
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel

@Composable
fun TabNavigation(authViewModel : AuthViewModel, companyViewModel: CompanyViewModel,
                  socialViewModel: SocialViewModel, profileViewModel: ProfileViewModel
) {
    
    // Main tab state
    var selectedTabIndex by remember { mutableIntStateOf(AppState.selectedMainTabIndex) }
    
    // Social tab state
    var selectedSocialTabIndex by remember { mutableIntStateOf(AppState.selectedSocialTabIndex) }
    
    // Profile tab state
    var selectedProfileTabIndex by remember { mutableIntStateOf(AppState.selectedProfileTabIndex) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> HomeNavHost(authViewModel, companyViewModel)
                1 -> UserBookingNavHost(authViewModel)
                2 -> SocialNavHost(
                    authViewModel = authViewModel,
	                socialViewModel = socialViewModel,
	                selectedSocialTabIndex = selectedSocialTabIndex
                )
                { newIndex ->
                    selectedSocialTabIndex = newIndex
                    AppState.selectedSocialTabIndex = newIndex
                }
                3 -> ProfileNavHost(
                    profileViewModel,
                    selectedProfileTabIndex = selectedProfileTabIndex
                )
                { newIndex ->
                    selectedProfileTabIndex = newIndex
                    AppState.selectedProfileTabIndex = newIndex
                }
                else -> Text("Error: Tab not found")
            }
        }
        NavigationBar {
            val tabs = listOf(
                TabItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
                TabItem("Booking", Icons.Filled.Search, Icons.Outlined.Search),
                TabItem("Social", Icons.Filled.ThumbUp, Icons.Outlined.ThumbUp),
                TabItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
            )

            tabs.forEachIndexed { index, tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex == index) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label
                        )
                    },
                    label = { Text(tab.label) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
    }
}

private data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    BaseCampTheme {
        TabNavigation(
	        authViewModel = viewModel(),
	        companyViewModel = viewModel(),
	        socialViewModel = viewModel(),
            profileViewModel = viewModel()
        )
    }
}