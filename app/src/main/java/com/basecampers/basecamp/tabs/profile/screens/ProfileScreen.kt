package com.basecampers.basecamp.tabs.profile.screens

import android.annotation.SuppressLint
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
import com.basecampers.basecamp.tabs.profile.models.profileRoutes
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileScreen(onNavigateToEdit: () -> Unit = {}) {
    
    // Access data from UserSession
    val profile by UserSession.profile.collectAsState()
    val companyProfile by UserSession.companyProfile.collectAsState()
    val company by UserSession.company.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image Placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // User Name
        Text(
            text = "${profile?.firstName ?: ""} ${profile?.lastName ?: ""}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        // EDIT BUTTON
        Button(
            onClick = { onNavigateToEdit() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Edit name")
        }
        
        // User Email
        Text(
            text = profile?.email ?: "",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
        
        // User Status Badge
        companyProfile?.status?.let { status ->
            val statusColor = when(status) {
                UserStatus.ADMIN -> Color.Red
                UserStatus.SUPER_USER -> Color.Blue
                UserStatus.USER -> Color.Green
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                modifier = Modifier.padding(4.dp),
                shape = RoundedCornerShape(16.dp),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = status.name,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = statusColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Scrollable content for all model data
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile Model Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Profile Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (profile != null) {
                            ModelDataItem("ID", profile?.id ?: "")
                            ModelDataItem("Email", profile?.email ?: "")
                            ModelDataItem("First Name", profile?.firstName ?: "")
                            ModelDataItem("Last Name", profile?.lastName ?: "")
                            ModelDataItem("Company List", profile?.companyList?.joinToString(", ") ?: "")
                        } else {
                            Text(
                                text = "No profile data found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Company Profile Model Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Company Profile Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (companyProfile != null) {
                            ModelDataItem("ID", companyProfile?.id ?: "")
                            ModelDataItem("Company ID", companyProfile?.companyId ?: "")
                            ModelDataItem("Bio", companyProfile?.bio ?: "")
                            ModelDataItem("Status", companyProfile?.status?.name ?: "")
                            ModelDataItem("Image URL", companyProfile?.imageUrl?.toString() ?: "No image")
                        } else {
                            Text(
                                text = "No company profile data found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Company Model Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Company Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (company != null) {
                            ModelDataItem("Company ID", company?.companyId ?: "")
                            ModelDataItem("Company Name", company?.companyName ?: "")
                            ModelDataItem("Owner UID", company?.ownerUID ?: "")
                            ModelDataItem("Bio", company?.bio ?: "")
                            ModelDataItem("Image URL", company?.imageUrl?.toString() ?: "No image")
                        } else {
                            Text(
                                text = "No company data found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Your Companies List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Companies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (profile?.companyList?.isNotEmpty() == true) {
                            Column {
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
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelDataItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = if (value.isNotEmpty()) value else "Not available",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CompanyListItem(
    companyId: String,
    isSelected: Boolean,
    onCompanySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCompanySelected(companyId) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Company Icon placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Company ID
        Text(
            text = companyId,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        // Selected indicator
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}