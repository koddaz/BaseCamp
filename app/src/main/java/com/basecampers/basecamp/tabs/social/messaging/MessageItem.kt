package com.basecampers.basecamp.tabs.social.messaging

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.social.messaging.models.Message
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
	message: Message,
	isFromCurrentUser: Boolean
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start
	) {
		Column(
			modifier = Modifier
				.widthIn(max = 300.dp)
				.clip(
					RoundedCornerShape(
						topStart = 16.dp,
						topEnd = 16.dp,
						bottomStart = if (isFromCurrentUser) 16.dp else 4.dp,
						bottomEnd = if (isFromCurrentUser) 4.dp else 16.dp
					)
				)
				.background(
					if (isFromCurrentUser)
						TextSecondary
					else
						CardBackground
				)
				.padding(12.dp)
		) {
			Text(
				text = message.content,
				color = if (isFromCurrentUser)
					MaterialTheme.colorScheme.onPrimary
				else
					MaterialTheme.colorScheme.onSurfaceVariant
			)
			
			Spacer(modifier = Modifier.height(4.dp))
			
			Text(
				text = message.senderName,
				style = MaterialTheme.typography.labelSmall,
				fontWeight = FontWeight.Bold,
				color = if (isFromCurrentUser)
					MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
				else
					MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
			)
		}
		
		Spacer(modifier = Modifier.height(2.dp))
		
		Text(
			text = formatTimestamp(message.timestamp),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.outline
		)
	}
}

private fun formatTimestamp(timestamp: Long): String {
	val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
	return dateFormat.format(Date(timestamp))
}