package com.basecampers.basecamp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

// Theme imports
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary
import com.basecampers.basecamp.ui.theme.BaseCampTheme

// Feature imports
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.booking.user.UserBookingNavHost
import com.basecampers.basecamp.tabs.home.HomeNavHost
import com.basecampers.basecamp.tabs.profile.ProfileNavHost
import com.basecampers.basecamp.tabs.social.SocialNavHost

@Composable
fun TabNavigation(authViewModel: AuthViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> HomeNavHost(authViewModel)
                1 -> UserBookingNavHost(authViewModel)
                2 -> SocialNavHost()
                3 -> ProfileNavHost(authViewModel)
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
        TabNavigation(authViewModel = viewModel())
    }
}