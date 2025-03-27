package com.basecampers.components

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NavButton(
    onClick: () -> Unit,
    title: String,

) {
        Button(onClick = onClick) {
            Text(title, color = colorScheme.primary)
        }
}



@Preview(showBackground = true)
@Composable
fun NavButtonPreview() {
    NavButton(onClick = { /*TODO*/ }, title = "Button")
}