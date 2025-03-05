package com.basecampers.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun NavButtonIcon(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
    ) {
    IconButton(onClick = { onClick }, modifier.size(64.dp).fillMaxSize()) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(54.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun NavIconPreview() {
    NavButtonIcon(onClick = {}, icon = Icons.Default.Home, contentDescription = "Home")
}
