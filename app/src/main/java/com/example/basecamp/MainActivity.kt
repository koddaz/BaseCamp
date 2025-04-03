package com.example.basecamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.ui.theme.BaseCampTheme
import com.example.basecamp.aRootFolder.Root
import com.example.basecamp.navigation.models.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel()

        setContent {
            BaseCampTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Root(authViewModel)
                }
            }
        }
    }
}




