package com.basecampers.basecamp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun PasswordPolicyInfo(
	visible: Boolean,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier
) {
	if (visible) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.5f))
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = null
				) {
					onDismiss()
				}
				.zIndex(10f)
		) {
			Card(
				modifier = Modifier
					.padding(16.dp)
					.align(Alignment.Center)
					// This prevents the click from passing through to the background
					.clickable(
						interactionSource = remember { MutableInteractionSource() },
						indication = null
					) {
						// Do nothing, just consume the click
					},
				shape = MaterialTheme.shapes.medium,
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surface
				),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 8.dp
				)
			) {
				Column(
					modifier = Modifier.padding(16.dp)
				) {
					Text(
						"Password Policy:",
						style = MaterialTheme.typography.titleMedium,
						modifier = Modifier.padding(bottom = 8.dp)
					)
					PasswordRequirementItem("At least 6 characters long")
					PasswordRequirementItem("Contains at least one uppercase letter")
					PasswordRequirementItem("Contains at least one number")
					PasswordRequirementItem("Contains at least one special character")
				}
			}
		}
	}
}

@Composable
fun PasswordRequirementItem(text: String) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.padding(vertical = 4.dp)
	) {
		Icon(
			imageVector = Icons.Default.Info,
			contentDescription = null,
			modifier = Modifier.size(16.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(modifier = Modifier.width(8.dp))
		Text(text, style = MaterialTheme.typography.bodyMedium)
	}
}