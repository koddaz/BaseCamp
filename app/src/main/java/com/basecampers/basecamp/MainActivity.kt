package com.basecampers.basecamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.basecampers.basecamp.aRootFolder.Root
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.ui.theme.BaseCampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel()

        setContent {
            BaseCampTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Root(authViewModel, innerPadding)
                }
            }
        }
    }
}




