
package com.basecampers.basecamp.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NavButton(
    onClick: () -> Unit,
    title: String,
    
    ) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primaryContainer,
            contentColor = colorScheme.onPrimaryContainer
        )) {
        Text(title)
    }
}

@Preview(showBackground = true)
@Composable
fun NavButtonPreview() {
    NavButton(onClick = { /*TODO*/ }, title = "Button")
}