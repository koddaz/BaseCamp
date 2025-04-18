package com.basecampers.basecamp.tabs.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.basecampers.basecamp.company.viewModel.CompanyViewModel
import com.basecampers.basecamp.ui.theme.*
import com.basecampers.basecamp.components.VerticalCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    companyViewModel: CompanyViewModel,
    onReportClick: () -> Unit
) {
    val userInfo by authViewModel.companyProfile.collectAsState()
    var showTestButtons by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                IconButton(onClick = { showTestButtons = !showTestButtons }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = TextPrimary
                    )
                }
            }

            // Test Buttons (only shown when menu is clicked)
            if (showTestButtons) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                authViewModel.logout()
                                companyViewModel.clearSelectedCompany()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Logout")
                        }

                        Button(
                            onClick = { authViewModel.isLoggedInFalse() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change isLoggedIn to False")
                        }
                        
                        Button(
                            onClick = { companyViewModel.clearSelectedCompany() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change hasSelectedCompany to False")
                        }

                        Button(
                            onClick = {
                                companyViewModel.registerUserToCompany(
                                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                                    companyId = "66a2bdbb-7218-48a3-ab86-4d1bd2de0728",
                                    onSuccess = {
                                        // Handle success
                                    },
                                    onError = { error ->
                                        // Handle error
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Register User to Company")
                        }
                    }
                }
            }

            // Report Problem Card
            VerticalCard(
                title = "Report a Problem",
                subtitle = "Need help?",
                description = "Let us know if you're experiencing any issues with the app or have suggestions for improvement.",
                buttonText = "Report",
                onButtonClick = onReportClick
            )

            // Quick Tips Card
            VerticalCard(
                title = "Quick Tips",
                subtitle = "Get Started",
                description = "Learn how to make the most of Basecamp with our quick tips and tutorials.",
                buttonText = "View Tips",
                onButtonClick = { /* TODO: Implement tips navigation */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        authViewModel = viewModel(),
        companyViewModel = viewModel(),
        onReportClick = {}
    )
}