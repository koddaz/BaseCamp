package com.example.basecamp.aRootFolder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.basecampers.components.NavButton

@Composable
fun LoadingScreen(tempFunction: () -> Unit) {

        Column(modifier = Modifier.fillMaxSize()) {
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