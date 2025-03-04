package com.example.basecamp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.components.NavButton
import com.example.basecamp.navigation.models.Routes

@Composable
fun LoadingScreen(tempFunction: () -> Unit) {


    Column(modifier = Modifier.fillMaxSize() ) {
        NavButton(
            onClick = {
                tempFunction()
            },
            title = "Next"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen(tempFunction = {})
}