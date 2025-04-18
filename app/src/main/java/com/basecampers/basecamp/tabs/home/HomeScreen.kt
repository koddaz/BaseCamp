package com.basecampers.basecamp.tabs.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyViewModel
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.PrimaryRed
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary
import androidx.compose.runtime.setValue
import com.basecampers.basecamp.components.VerticalCard

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    companyViewModel: CompanyViewModel,
    onReportClick: () -> Unit
) {
    val userInfo by authViewModel.companyProfile.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {  // Wrap everything in a Box for FAB overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            // Top Bar with User Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryAqua)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                // Name and Room number with padding
                Column(
                    modifier = Modifier.padding(start = 12.dp)
                ) {
                    Text(
                        text = "John Doe",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Room 101",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Vertical Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // First Card (Report Problem)
                VerticalCard(
                    title = "Report a problem",
                    subtitle = "Need help?",
                    description = "Let us know if something is not working right. We are here to help 24/7!",
                    buttonText = "Report",
                    onButtonClick = onReportClick,
                    modifier = Modifier.weight(1f)
                )
                
                // Second Card (Book a Room)
                VerticalCard(
                    title = "Book a Room",
                    subtitle = "Quick Booking",
                    description = "Book a meeting room instantly. Check availability and reserve in seconds!",
                    buttonText = "Book Now",
                    onButtonClick = onReportClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // Quick Tips Horizontal Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quick Tips",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Helpful information for getting started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tips",
                        tint = PrimaryRed
                    )
                }
            }
        }

        // FAB with dropdown menu
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = { showMenu = !showMenu },
                containerColor = PrimaryRed,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "More Options"
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .width(280.dp)
                    .background(CardBackground)
            ) {
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        authViewModel.logout()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Change isLoggedIn to False") },
                    onClick = {
                        authViewModel.isLoggedInFalse()
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Register User to Company") },
                    onClick = {
                        authViewModel.registerUserToCompany(
                            companyId = "66a2bdbb-7218-48a3-ab86-4d1bd2de0728"
                        )
                        showMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Change hasSelectedCompany to False") },
                    onClick = {
                        companyViewModel.clearSelectedCompany()
                        showMenu = false
                    }
                )
            }
        }
    }
}

@Composable
fun ReportProblemCard(
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // ... rest of the existing card content ...
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val authViewModel: AuthViewModel = viewModel()
    val companyViewModel: CompanyViewModel = viewModel()
    
    HomeScreen(
        authViewModel = authViewModel,
        companyViewModel = companyViewModel,
        onReportClick = {}
    )
}