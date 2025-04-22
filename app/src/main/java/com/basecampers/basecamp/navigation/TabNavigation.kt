package com.basecampers.basecamp.navigation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

// Theme imports
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary
import com.basecampers.basecamp.ui.theme.BaseCampTheme

// Feature imports
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.tabs.booking.user.viewModel.UserBookingViewModel
import com.basecampers.basecamp.tabs.booking.user.UserBookingNavHost
import com.basecampers.basecamp.tabs.home.HomeNavHost
import com.basecampers.basecamp.tabs.profile.navHost.ProfileNavHost
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.tabs.social.navHost.SocialNavHost
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel

@Composable
fun TabNavigation(
    authViewModel: AuthViewModel,
    companyViewModel: CompanyViewModel,
    socialViewModel: SocialViewModel,
    profileViewModel: ProfileViewModel
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
                0 -> HomeNavHost(
                    authViewModel = authViewModel,
                    companyViewModel = companyViewModel
                )
                1 -> {
                    UserBookingNavHost()
                }
                2 -> SocialNavHost(
                    socialViewModel = socialViewModel,
                    selectedSocialTabIndex = selectedSocialTabIndex,
                    onSocialTabSelected = { newIndex ->
                        selectedSocialTabIndex = newIndex
                        AppState.selectedSocialTabIndex = newIndex
                    }
                )
                3 -> ProfileNavHost(
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel,
                    companyViewModel = companyViewModel,
                    selectedProfileTabIndex = selectedProfileTabIndex,
                    onProfileTabSelected = { newIndex ->
                        selectedProfileTabIndex = newIndex
                        AppState.selectedProfileTabIndex = newIndex
                    }
                )
                else -> Text("Error: Tab not found")
            }
        }
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = CardBackground,
            tonalElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    TabItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
                    TabItem("Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
                    TabItem("Forum", Icons.Filled.Forum, Icons.Outlined.Forum),
                    TabItem("Profile", Icons.Filled.Person, Icons.Outlined.Person)
                )

                tabs.forEachIndexed { index, tab ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTabIndex = index }
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icon and Text
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedTabIndex == index) 
                                    tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.label,
                                tint = if (selectedTabIndex == index) 
                                    TextPrimary else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = tab.label,
                                color = if (selectedTabIndex == index) 
                                    TextPrimary else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        
                        // Bottom indicator line
                        if (selectedTabIndex == index) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .height(4.dp)
                                    .width(40.dp)
                                    .background(
                                        color = TextPrimary,
                                        shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                                    )
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
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