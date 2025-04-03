package com.example.basecamp.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CustomColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .padding(16.dp)
    ) {
        if (title.isNotEmpty()) {
            Text(text = title, style = typography.titleLarge)
        }
        content()
    }
}