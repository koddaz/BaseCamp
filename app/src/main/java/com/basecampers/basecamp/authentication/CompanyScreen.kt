package com.basecampers.basecamp.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.authentication.viewModels.Company
import com.basecampers.basecamp.authentication.viewModels.USERVIEWMODEL

@Composable
fun CompanyScreen(authViewModel : AuthViewModel) {

    var memberCompanies by remember { mutableStateOf<List<Company>>(emptyList()) }
    var nonMemberCompanies by remember { mutableStateOf<List<Company>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var showJoinButton by remember { mutableStateOf(false) }
    var isJoining by remember { mutableStateOf(false) }

    // Load companies when the screen is first shown
    LaunchedEffect(true) {
        authViewModel.getUserCompanies { memberList, nonMemberList ->
            memberCompanies = memberList
            nonMemberCompanies = nonMemberList
            isLoading = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Your Companies",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Member companies section
                Text(
                    text = "Companies You're a Member Of",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (memberCompanies.isEmpty()) {
                    Text(
                        text = "You're not a member of any company yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp)
                    ) {
                        items(memberCompanies) { company ->
                            CompanyCard(company)
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = 8.dp))

                // Join company section
                Text(
                    text = "Join a Company",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Dropdown to select company
                Box(modifier = Modifier.padding(vertical = 8.dp)) {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = "Company"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedCompany?.name ?: "Select a Company",
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = if (expanded) "Collapse" else "Expand"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (nonMemberCompanies.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No companies available") },
                                onClick = { expanded = false }
                            )
                        } else {
                            nonMemberCompanies.forEach { company ->
                                DropdownMenuItem(
                                    text = { Text(company.name) },
                                    onClick = {
                                        selectedCompany = company
                                        showJoinButton = true
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Join button
                if (showJoinButton && selectedCompany != null) {
                    Button(
                        onClick = {
                            isJoining = true
                            selectedCompany?.id?.let { companyId ->
                                authViewModel.joinCompany(companyId) { success ->
                                    if (success) {
                                        // Refresh company lists
                                        authViewModel.getUserCompanies { memberList, nonMemberList ->
                                            memberCompanies = memberList
                                            nonMemberCompanies = nonMemberList
                                            selectedCompany = null
                                            showJoinButton = false
                                            isJoining = false
                                        }
                                    } else {
                                        isJoining = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isJoining
                    ) {
                        if (isJoining) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Text("Join ${selectedCompany?.name}")
                    }
                }

                // Add new company button
                FloatingActionButton(
                    onClick = { /* Navigate to add company screen */ },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Company")
                }
            }
        }
    }
}

@Composable
fun CompanyCard(company: Company) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = "Company",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = company.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ID: ${company.id}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(onClick = { /* Navigate to company details */ }) {
                Text("View")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompanyScreenPreview() {
    CompanyScreen(viewModel())
}