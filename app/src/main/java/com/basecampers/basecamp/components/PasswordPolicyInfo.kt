package com.basecampers.basecamp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.basecampers.basecamp.ui.theme.*

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
					.clickable(
						interactionSource = remember { MutableInteractionSource() },
						indication = null
					) {
						// Do nothing, just consume the click
					},
				shape = RoundedCornerShape(16.dp),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.surface
				),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 8.dp
				)
			) {
				Column(
					modifier = Modifier
						.padding(24.dp)
						.verticalScroll(rememberScrollState())
				) {
					Text(
						"Password Requirements",
						style = MaterialTheme.typography.titleLarge.copy(
							fontWeight = FontWeight.Bold
						),
						color = TextPrimary,
						modifier = Modifier.padding(bottom = 16.dp)
					)
					
					PasswordRequirementItem("At least 8 characters long")
					Spacer(modifier = Modifier.height(8.dp))
					PasswordRequirementItem("Contains at least one uppercase letter")
					Spacer(modifier = Modifier.height(8.dp))
					PasswordRequirementItem("Contains at least one number")
					Spacer(modifier = Modifier.height(8.dp))
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
			modifier = Modifier.size(20.dp),
			tint = SecondaryAqua
		)
		Spacer(modifier = Modifier.width(12.dp))
		Text(
			text = text,
			style = MaterialTheme.typography.bodyLarge,
			color = TextSecondary
		)
	}
}