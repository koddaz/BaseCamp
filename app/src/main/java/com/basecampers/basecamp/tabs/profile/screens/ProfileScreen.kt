package com.basecampers.basecamp.tabs.profile.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.UserStatus
import com.basecampers.basecamp.components.BasecampCard
import com.basecampers.basecamp.tabs.profile.models.profileRoutes
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.basecampers.basecamp.ui.theme.AppBackground
import com.basecampers.basecamp.ui.theme.CardBackground
import com.basecampers.basecamp.ui.theme.PrimaryRed
import com.basecampers.basecamp.ui.theme.SecondaryAqua
import com.basecampers.basecamp.ui.theme.TextPrimary
import com.basecampers.basecamp.ui.theme.TextSecondary

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(onNavigateToEdit: () -> Unit = {}) {
    
    // Access data from UserSession
    val profile by UserSession.profile.collectAsState()
    val companyProfile by UserSession.companyProfile.collectAsState()
    val company by UserSession.company.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Background Pattern
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(SecondaryAqua.copy(alpha = 0.1f))
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Profile Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    // Profile Image with Border
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(2.dp, SecondaryAqua),
                        color = CardBackground,
                        modifier = Modifier.size(120.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile Picture",
                                tint = SecondaryAqua,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name
                    Text(
                        text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    
                    // User Email
                    Text(
                        text = profile?.email ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Edit Button
                    OutlinedButton(
                        onClick = onNavigateToEdit,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SecondaryAqua),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SecondaryAqua
                        )
                    ) {
                        Text("Edit Profile")
                    }
                    
                    // User Status Badge
                    companyProfile?.status?.let { status ->
                        val (statusColor, statusBackground) = when(status) {
                            UserStatus.ADMIN -> PrimaryRed to PrimaryRed.copy(alpha = 0.1f)
                            UserStatus.SUPER_USER -> SecondaryAqua to SecondaryAqua.copy(alpha = 0.1f)
                            UserStatus.USER -> Color(0xFF4CAF50) to Color(0xFF4CAF50).copy(alpha = 0.1f)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = statusBackground,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = status.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = statusColor,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
            
            // Profile Data Card
            item {
                BasecampCard(
                    title = "Profile Information",
                    subtitle = "Your personal information",
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (profile != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModelDataItem("First Name", profile?.firstName ?: "")
                            ModelDataItem("Last Name", profile?.lastName ?: "")
                            ModelDataItem("Email", profile?.email ?: "")
                        }
                    } else {
                        Text(
                            text = "No profile data found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // Company Profile Card
            item {
                BasecampCard(
                    title = "Company Profile",
                    subtitle = "Your role and position",
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (companyProfile != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModelDataItem("Status", companyProfile?.status?.name ?: "")
                            ModelDataItem("Bio", companyProfile?.bio ?: "No bio added")
                        }
                    } else {
                        Text(
                            text = "No company profile data found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // Company Data Card
            item {
                BasecampCard(
                    title = "Current Company",
                    subtitle = "Information about your current company",
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    if (company != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModelDataItem("Company Name", company?.companyName ?: "")
                            ModelDataItem("Bio", company?.bio ?: "No company bio")
                        }
                    } else {
                        Text(
                            text = "No company data found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            
            // Your Companies Card
            item {
                BasecampCard(
                    title = "Your Companies",
                    subtitle = "Companies you're a member of",
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    if (profile?.companyList?.isNotEmpty() == true) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            profile?.companyList?.forEach { companyId ->
                                CompanyListItem(
                                    companyId = companyId,
                                    isSelected = companyId == UserSession.selectedCompanyId.value,
                                    onCompanySelected = { /* Call your selectCompany function */ }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No companies added yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelDataItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = if (value.isNotEmpty()) value else "Not available",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}

@Composable
fun CompanyListItem(
    companyId: String,
    isSelected: Boolean,
    onCompanySelected: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) SecondaryAqua.copy(alpha = 0.1f) else CardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(companyId) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Company Icon with background
            Surface(
                shape = CircleShape,
                color = if (isSelected) SecondaryAqua else Color.LightGray,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Company ID with better typography
            Text(
                text = companyId,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) SecondaryAqua else TextPrimary,
                modifier = Modifier.weight(1f)
            )
            
            // Selected indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = SecondaryAqua
                )
            }
        }
    }
}