package com.basecampers.basecamp.aRootFolder

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.navigation.TabNavigation
import com.basecampers.basecamp.authentication.AuthNavHost
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyNavHost
import com.basecampers.basecamp.company.CompanyViewModel
import com.basecampers.basecamp.tabs.profile.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel
import kotlinx.coroutines.delay

// Root.kt with logging
@Composable
fun Root(
    authViewModel: AuthViewModel = viewModel(),
    companyViewModel: CompanyViewModel = viewModel(),
    socialViewModel: SocialViewModel = viewModel(),
    innerPadding: PaddingValues
) {
    var isLoading by remember { mutableStateOf(true) }
    val isLoggedIn by authViewModel.loggedin.collectAsState()
    val hasSelectedCompany by companyViewModel.hasSelectedCompany.collectAsState()
    
    LaunchedEffect(isLoggedIn, hasSelectedCompany) {
        Log.d("UserSessionFlow", "State changed - isLoggedIn: $isLoggedIn, hasSelectedCompany: $hasSelectedCompany")
        
        if (!isLoggedIn) {
            Log.d("UserSessionFlow", "Not logged in - clearing session")
            UserSession.clearSession()
        } else {
            val userId = authViewModel.getCurrentUserUid()
            Log.d("UserSessionFlow", "Logged in with userId: $userId")
            
            if (userId != null) {
                Log.d("UserSessionFlow", "Initializing UserSession with userId: $userId")
                UserSession.initialize(userId)
                
                Log.d("UserSessionFlow", "Fetching user profile model")
                authViewModel.fetchCurrentUserModel()
                
                if (hasSelectedCompany) {
                    val companyId = companyViewModel.currentCompanyId.value
                    Log.d("UserSessionFlow", "Has selected company: $companyId")
                    
                    if (companyId != null) {
                        loadCompanyData(authViewModel, userId, companyId)
                    }
                } else {
                    Log.d("UserSessionFlow", "No company selected - clearing company data")
                    clearCompanyData()
                }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        Log.d("UserSessionFlow", "Initial app loading - checking login state")
        authViewModel.checkLoggedin()
        delay(1500)
        Log.d("UserSessionFlow", "Initial loading complete")
        isLoading = false
    }
    
    //Om vi fastnar så tvingas vi bort från loading screen efter 5 sek.
    LaunchedEffect(Unit) {
        delay(5000)
        if (isLoading) {
            Log.d("UserSessionFlow", "Safety timeout triggered - forcing loading to complete")
            isLoading = false
        }
    }
    
    Column(Modifier.fillMaxSize().padding(innerPadding)) {
        if (isLoading) {
            LoadingScreen()
        } else if (!isLoggedIn) {
            AuthNavHost(authViewModel)
        } else if (!hasSelectedCompany) {
            CompanyNavHost(companyViewModel, authViewModel)
        } else {
            TabNavigation(authViewModel, companyViewModel, socialViewModel)
        }
    }
}

private fun loadCompanyData(authViewModel: AuthViewModel, userId: String, companyId: String) {
    Log.d("UserSessionFlow", "Loading company data - userId: $userId, companyId: $companyId")
    UserSession.setSelectedCompanyId(companyId)
    authViewModel.fetchCompanyData(companyId)
    authViewModel.fetchCompanyProfileData(userId, companyId)
}

private fun clearCompanyData() {
    Log.d("UserSessionFlow", "Clearing company data")
    UserSession.setSelectedCompanyId(null)
    UserSession.setCompany(CompanyModel())
    UserSession.setCompanyProfile(CompanyProfileModel())
}

@Preview(showBackground = true)
@Composable
fun RootPreview() {
    val authViewModel = viewModel<AuthViewModel>()
    val companyViewModel = viewModel<CompanyViewModel>()
    val socialViewModel = viewModel<SocialViewModel>()
    Root(
        authViewModel = authViewModel,
        companyViewModel = companyViewModel,
        socialViewModel = socialViewModel,
        innerPadding = PaddingValues()
    )
}