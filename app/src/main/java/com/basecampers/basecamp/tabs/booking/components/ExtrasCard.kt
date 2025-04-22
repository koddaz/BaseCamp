package com.basecampers.basecamp.tabs.booking.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.tabs.booking.models.BookingExtra
import com.basecampers.basecamp.ui.theme.*

@Composable
fun ExtrasCard(
    modifier: Modifier = Modifier,
    extra: BookingExtra,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SecondaryAqua.copy(alpha = 0.1f) else CardBackground,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) SecondaryAqua else Color.Gray.copy(alpha = 0.2f)
        ),
        tonalElevation = if (isSelected) 0.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon and Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Extra Icon with background
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) 
                        SecondaryAqua.copy(alpha = 0.1f) 
                    else 
                        Color.Gray.copy(alpha = 0.05f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = if (isSelected) SecondaryAqua else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Name and Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = extra.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) SecondaryAqua else TextPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = extra.info,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            
            // Price and Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Price
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) 
                        SecondaryAqua.copy(alpha = 0.1f) 
                    else 
                        Color.Gray.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "€${extra.price}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) SecondaryAqua else TextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                
                // Action Icon
                Icon(
                    imageVector = if (isSelected) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (isSelected) "Remove Extra" else "Add Extra",
                    tint = if (isSelected) SecondaryAqua else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
} 