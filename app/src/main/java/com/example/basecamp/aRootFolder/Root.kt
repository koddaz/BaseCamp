package com.example.basecamp.aRootFolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.basecampers.navigation.TabNavigation

@Composable
fun Root() {
    var isLoading by remember { mutableStateOf(true) }
    val tempFunction = { isLoading = false }
    Column(Modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingScreen(
                tempFunction = tempFunction,
            )
        } else {
            TabNavigation()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    Root()
}