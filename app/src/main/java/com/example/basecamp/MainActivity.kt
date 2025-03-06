package com.basecampers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.basecampers.Authentication.LoginScreen
import com.basecampers.navigation.RootNav


import com.basecampers.ui.theme.BaseCampTheme
import com.example.basecamp.authentication.LoginStart

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaseCampTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                   LoginScreen()
                   // LoginStart()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BaseCampTheme {

       LoginScreen()
//LoginStart()
    }
}
