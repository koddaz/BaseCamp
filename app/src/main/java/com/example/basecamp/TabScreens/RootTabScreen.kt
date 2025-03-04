package com.example.basecamp.TabScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.components.NavButtonIcon
import com.example.basecamp.navigation.TabNavigation
import com.example.basecamp.navigation.models.Routes
import com.example.basecamp.ui.theme.BaseCampTheme

@Composable
fun RootTabScreen() {

    val navController = rememberNavController()
    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            TabNavigation()
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color = MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
            NavButtonIcon(
                onClick = { },
                icon = Icons.Default.Home,
                contentDescription = "home",
                // Adjust the size as needed
            )
            NavButtonIcon(
                onClick = {  },
                icon = Icons.Default.Search,
                contentDescription = "booking"
            )
            NavButtonIcon(
                onClick = { navController.navigate(route = Routes.PROFILE) },
                icon = Icons.Default.Person,
                contentDescription = "Profile"
            )
            NavButtonIcon(
                onClick = { },
                icon = Icons.Default.AccountBox,
                contentDescription = "social"
            )
        }
    }
}

@Preview
@Composable
fun RootTabScreenPreview() {
    BaseCampTheme {
    RootTabScreen()
        }
}