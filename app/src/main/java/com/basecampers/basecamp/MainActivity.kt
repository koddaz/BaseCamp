package com.basecampers.basecamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.aRootFolder.Root
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.company.CompanyViewModel
import com.basecampers.basecamp.tabs.social.viewModel.SocialViewModel
import com.basecampers.basecamp.ui.theme.BaseCampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel()
        val companyViewModel = CompanyViewModel()
        val socialViewModel = SocialViewModel()

        setContent {
            BaseCampTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Root(
                        authViewModel = authViewModel,
                        companyViewModel = companyViewModel,
                        socialViewModel = socialViewModel,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}




