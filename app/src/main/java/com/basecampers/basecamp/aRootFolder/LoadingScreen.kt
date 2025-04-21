package com.basecampers.basecamp.aRootFolder

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.R
import com.basecampers.basecamp.ui.theme.SecondaryAqua

/**
 * Simple loading screen with a progress indicator.
 * Does not handle any logic - purely for display.
 */
@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Basecamp Logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.basecamp_logo),
                contentDescription = "Basecamp Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Basecamp",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(4.dp),
            color = SecondaryAqua
        )
    }
}
