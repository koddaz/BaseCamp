package com.basecampers.basecamp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.ui.theme.SecondaryAqua

@Composable
fun PasswordInfoButton(
	modifier: Modifier = Modifier,
	onInfoClick: () -> Unit
) {
	Icon(
		imageVector = Icons.Default.Info,
		contentDescription = "Password Policy Information",
		modifier = modifier
			.size(24.dp)
			.clickable { onInfoClick() },
		tint = SecondaryAqua
	)
}