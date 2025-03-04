package com.example.basecamp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NavButton(
    onClick: () -> Unit,
    title: String,

) {
        Button(onClick = { onClick }) {
            Text(title)
        }
}



@Preview(showBackground = true)
@Composable
fun NavButtonPreview() {
    NavButton(onClick = { /*TODO*/ }, title = "Button")
}