package com.basecampers.basecamp.tabs.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.ui.theme.*

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onReportClick: () -> Unit
) {
    val userInfo by authViewModel.companyProfile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        // User Profile Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                color = SecondaryAqua
            ) {
                // Placeholder for user avatar
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInfo?.id?.take(2)?.uppercase() ?: "PJ",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = userInfo?.id ?: "Pretty Jones",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = userInfo?.companyId ?: "Room 1012",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Cards Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Report Problem Card
            ReportProblemCard(onReportClick = onReportClick)

            // My Bookings Card
            MyBookingsCard()
        }

        // Quick Tips Card
        QuickTipsHorizontalCard()
    }
}

@Composable
fun ReportProblemCard(
    onReportClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .width(200.dp)
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Image + Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryAqua),
                contentAlignment = Alignment.TopEnd
            ) {
                // Placeholder image icon
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )

                // Tag
                Text(
                    text = "TAG",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(PrimaryRed, shape = RoundedCornerShape(50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .padding(top = 8.dp, end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Content
            Text(
                text = "Report a problem",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Need help?",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Let us know if something is not working right. We are here to help 24/7!",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Outlined Button
            OutlinedButton(
                onClick = onReportClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, PrimaryRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Report",
                    color = PrimaryRed
                )
            }
        }
    }
}

@Composable
fun MyBookingsCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .width(200.dp)
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Content
            Text(
                text = "My Bookings",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Quick Access",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Access all your booking and make quick changes in reservations or rooms.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Outlined Button
            OutlinedButton(
                onClick = { /* TODO: Implement bookings action */ },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SecondaryAqua),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Open",
                    color = SecondaryAqua
                )
            }
        }
    }
}

@Composable
fun QuickTipsHorizontalCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SecondaryAqua),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column {
                Text(
                    text = "Quick Tips",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Helpful guides and tips for your stay",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        authViewModel = AuthViewModel(),
        onReportClick = {}
    )
}