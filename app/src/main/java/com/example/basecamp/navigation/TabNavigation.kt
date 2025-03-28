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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.basecampers.ui.theme.BaseCampTheme
import com.example.basecamp.navigation.models.AuthViewModel
import com.example.basecamp.tabs.booking.BookingNavHost
import com.example.basecamp.tabs.home.HomeNavHost
import com.example.basecamp.tabs.profile.ProfileNavHost
import com.example.basecamp.tabs.social.SocialNavHost


@Composable
fun TabNavigation(authViewModel : AuthViewModel) {
    var selectedItem by remember { mutableIntStateOf(0) }
    var navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            if (selectedItem == 0) {
                HomeNavHost(authViewModel)
            } else if (selectedItem == 1) {
                BookingNavHost()
            } else if (selectedItem == 2) {
                SocialNavHost()
            } else if (selectedItem == 3) {
                ProfileNavHost(authViewModel)
            } else {
                Text("FEL FINNS INTE")
            }


        }
        NavigationBar() {
            val items = listOf("Home", "Booking", "Social", "Profile")
            val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.ThumbUp, Icons.Filled.Person)
            val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Search, Icons.Outlined.ThumbUp, Icons.Outlined.Person)
            // var selectedItem by remember { mutableIntStateOf(0) }

            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                            contentDescription = item
                        )
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = { selectedItem = index }
                )
            }
        }
    }
}


@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        windowInsets = windowInsets,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    BaseCampTheme {
        TabNavigation(authViewModel = viewModel())
    }
}