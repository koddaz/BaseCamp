package com.basecampers.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.basecampers.ui.theme.BaseCampTheme
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.BookingNavHost
import com.example.basecamp.tabs.home.HomeNavHost
import com.example.basecamp.tabs.profile.ProfileNavHost
import com.example.basecamp.tabs.social.navHost.SocialNavHost


@Composable
fun TabNavigation(authViewModel : AuthViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> HomeNavHost(authViewModel)
                1 -> BookingNavHost()
                2 -> SocialNavHost()
                3 -> ProfileNavHost(authViewModel)
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
        TabNavigation(authViewModel = viewModel())
    }
}