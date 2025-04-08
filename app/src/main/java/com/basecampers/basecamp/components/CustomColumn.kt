package com.basecampers.basecamp.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomColumn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    imageVector: ImageVector? = null,
    title: String = "",
    content: @Composable () -> Unit,

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black)
            .padding(8.dp)
    ) {
        if (title.isNotEmpty()) {
            Row(modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = typography.titleLarge, textAlign = TextAlign.Left)
                Spacer(modifier = Modifier.weight(1f))
                if (imageVector != null) {
                    IconButton(
                        onClick = { onClick() },
                    ) {
                        Icon(
                            imageVector = imageVector,
                            contentDescription = "Icon"
                        )
                    }
                }
            }
        }
        content()
    }
}