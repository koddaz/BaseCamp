package com.basecampers.basecamp.tabs.booking.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.basecampers.basecamp.ui.theme.*

data class RoomType(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val features: List<String>,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSelectionView(
    onNavigateBack: () -> Unit,
    onRoomSelected: (RoomType) -> Unit
) {
    // Sample room data - replace with actual data from ViewModel
    val rooms = remember {
        listOf(
            RoomType(
                id = "single_high",
                name = "Single High Floors",
                description = "Spacious single room on higher floors with city views",
                price = 1200.0,
                features = listOf("City View", "High Floor", "Single Bed", "Private Bathroom")
            ),
            RoomType(
                id = "single_view",
                name = "Single View",
                description = "Single room with beautiful city views",
                price = 1100.0,
                features = listOf("City View", "Single Bed", "Private Bathroom")
            ),
            RoomType(
                id = "single_large",
                name = "Single View Large Bed",
                description = "Single room with large bed and city views",
                price = 1300.0,
                features = listOf("City View", "Large Bed", "Private Bathroom")
            ),
            RoomType(
                id = "single_l",
                name = "Single L",
                description = "L-shaped single room with ample space",
                price = 1000.0,
                features = listOf("L-Shaped Layout", "Single Bed", "Private Bathroom")
            ),
            RoomType(
                id = "single_l_view",
                name = "Single L View",
                description = "L-shaped single room with city views",
                price = 1150.0,
                features = listOf("L-Shaped Layout", "City View", "Single Bed", "Private Bathroom")
            )
        )
    }

    var selectedRoom by remember { mutableStateOf<RoomType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Select Room Type",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AppBackground,
                titleContentColor = TextPrimary
            )
        )

        // Room List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rooms) { room ->
                RoomCard(
                    room = room,
                    isSelected = room.id == selectedRoom?.id,
                    onClick = {
                        selectedRoom = room
                    }
                )
            }
        }

        // Continue Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = AppBackground,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = {
                    selectedRoom?.let { onRoomSelected(it) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedRoom != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryAqua,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomCard(
    room: RoomType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                SecondaryAqua.copy(alpha = 0.1f) 
            else 
                CardBackground
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) 
                SecondaryAqua 
            else 
                Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$${String.format("%.2f", room.price)}/month",
                        style = MaterialTheme.typography.bodyLarge,
                        color = SecondaryAqua
                    )
                }
                Icon(
                    imageVector = if (room.features.contains("City View")) 
                        Icons.Default.Visibility 
                    else 
                        Icons.Default.Bed,
                    contentDescription = if (room.features.contains("City View")) 
                        "City View" 
                    else 
                        "Standard Room",
                    tint = SecondaryAqua
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = room.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Features
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                room.features.forEach { feature ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SecondaryAqua.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = feature,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryAqua
                        )
                    }
                }
            }
        }
    }
} 