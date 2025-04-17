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
import com.basecampers.basecamp.company.viewModels.CompanyViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    companyViewModel: CompanyViewModel,
    onReportClick: () -> Unit
) {
    val userInfo by authViewModel.companyProfile.collectAsState()

    Column(verticalArrangement = Arrangement.Center) {
        Text("Home Screen", style = MaterialTheme.typography.titleLarge)
        Button(onClick = {
            authViewModel.logout()
        }) {
            Text("Logout")
        }

        Button(onClick = {
            authViewModel.isLoggedInFalse()
        }) {
            Text("Change isLoggedIn to False")
        }

        Button(onClick = {
            authViewModel.registerUserToCompany(
                companyId = "66a2bdbb-7218-48a3-ab86-4d1bd2de0728"
            )
        }) {
            Text("Register User to Company --> TEST FUNKAR BARA FÖR ETT FÖRETAG")
        }

        Button(onClick = {
            companyViewModel.clearSelectedCompany()
        }) {
            Text("Change hasSelectedCompany to False")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        authViewModel = AuthViewModel(),
        companyViewModel = CompanyViewModel(),
        onReportClick = {}
    )
}