package com.basecampers.basecamp.tabs.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.basecampers.basecamp.authentication.viewModels.AuthViewModel
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProfileScreen(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel = viewModel()) {


//    val profileCount by profileViewModel.profileCount.collectAsState()


    val userInfo by authViewModel.userInfo.collectAsState()
    val uid = authViewModel.getCurrentUserUid()
    
    // Observe profile from Room
    val profile by remember(uid) {
        uid?.let { profileViewModel.observeProfile(it) } ?: flowOf(null)
    }.collectAsState(initial = null)
    
    var isRefreshing by remember { mutableStateOf(false) }
    
    //var profileCount by remember { mutableStateOf(0) }
//    Log.d("ProfileScreen", "Current profileCount: $profileCount")


   // LaunchedEffect(Unit) {
      //  profileCount = profileViewModel.getProfileCount()
   // }

   // LaunchedEffect(uid) {
     //   uid?.let {
       //     isRefreshing = true
         //   profileViewModel.refreshProfile(it)
           // isRefreshing = false
    //    }
   // }



    LaunchedEffect(uid) {
        Log.d("ProfileScreen", "LaunchedEffect(uid) triggered. uid: $uid")
        if (uid != null) {
            isRefreshing = true
            profileViewModel.refreshProfile(uid)
            isRefreshing = false
        }
    }







    Spacer(modifier = Modifier.height(16.dp))
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        
//        Text("Profiles in database: $profileCount")
        
        // Data source indicator
        Text(
            "Data source: ${if (profile != null) "Room Database" else "Not found in Room"}",
            style = MaterialTheme.typography.labelMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Profile data display
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Text("Profile Info", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Username: ${userInfo?.userName ?: "N/A"}")
                Text("Email: ${userInfo?.email ?: "N/A"}")
                Text("Bio: ${userInfo?.bio ?: "N/A"}")
                Text("Status: ${userInfo?.status ?: "N/A"}")
                Text("Company: ${userInfo?.companyName ?: "N/A"}")
//                Text("Profiles in database: $profileCount")
                profile?.let {
                    if (it.bio.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bio: ${it.bio}")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status: ${it.status}")
                    
                    if (it.companyName.isNotEmpty()) {
                        Text("Company: ${it.companyName}")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Manual refresh button
        Button(
            onClick = {
                uid?.let {
                    isRefreshing = true
                    profileViewModel.refreshProfile(it)
                    isRefreshing = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh Profile Data")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { authViewModel.deleteUser() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
        ) {
            Text("Delete Account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreviewScreen() {
    ProfileScreen(authViewModel = viewModel())
}