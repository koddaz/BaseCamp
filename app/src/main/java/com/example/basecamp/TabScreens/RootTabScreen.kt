package com.example.basecamp.TabScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.value.Value
import com.example.basecamp.components.NavButtonIcon
import com.example.basecamp.navigation.TabNavigation
import com.example.basecamp.navigation.models.Routes
import com.example.basecamp.ui.theme.BaseCampTheme


sealed class TabBarItem(
    val image: ImageVector,
    val route: String,

) {
    data object Home : TabBarItem(
        image = Icons.Default.Home,
        route = Routes.HOME,

    )

    data object Booking : TabBarItem(
        image = Icons.Default.Search,
        route = Routes.BOOKING,
    )
    
    data object Profile : TabBarItem(
        image = Icons.Default.Person,
        route = Routes.PROFILE,
    )

    data object Social : TabBarItem(
        image = Icons.Default.AccountBox,
        route = Routes.SOCIAL,
    )
}

@Composable
fun TabComponent(
    modifier: Modifier = Modifier,
    item: TabBarItem,
    isSelected: Boolean,
) {
    Column() {

        Icon(
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            imageVector = item.image
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(item.route)

    }
}

@Composable
fun BottomTabBar(
    modifier: Modifier = Modifier,
    items: List<TabBarItem>,
    onSelect: (TabBarItem) -> Unit,
) {
    var selectedItem: TabBarItem? by remember {
        mutableStateOf(TabBarItem.Home)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = MaterialTheme.colorScheme.onBackground),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { component ->
                TabComponent(
                    modifier = Modifier.clickable {
                        selectedItem = component
                        onSelect(component)
                    },
                    item = component,
                    isSelected = selectedItem == component
                )
            }
        }
    }
}

/*
class RootNavigator(
    private val navigation: StackNavigation <RootComponent.Config>,
    private val screenStack: Value<ChildStack<*, RootComponent.Child>>
) {
    fun showTabITem(item: TabBarItem) {
        is TabBarItem.Home -> navigation.bringToFront(RootComponent.Config.Home)
        is TabBarItem.Booking -> navigation.bringToFront(RootComponent.Config.Booking)
        is TabBarItem.Profile -> navigation.bringToFront(RootComponent.Config.Profile)
        is TabBarItem.Social -> navigation.bringToFront(RootComponent.Config.Social)
    }
}

@Composable
fun RootContent(rootComonent: RootComponent) {
    val navigator = remember { rootComponent.getNavigator()}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomTabBar(items = listOf(
                TabBarItem.Home,
                TabBarItem.Booking,
                TabBarItem.Profile,
                TabBarItem.Social
            ),
                onSelect = {
                    navigator.showTabITem(it)
                }
            )
        }) {
        Children(rootComponent.stack) { child ->
            when (child.instance) {
                is RootComponent.Child.Home -> HomeTab()
                is RootComponent.Child.Booking -> BookingContent(child.instance.component)
                is RootComponent.Child.Profile -> ProfileContent(child.instance.component)
        }
    }

    }
}

*/

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