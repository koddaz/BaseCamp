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


    val profile by authViewModel.profile.collectAsState()
    val companyProfile by authViewModel.companyProfile.collectAsState()
    val uid = authViewModel.getCurrentUserUid()
    
    // Observe profile from Room
    val roomProfile by remember(uid) {
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
            "Data source: ${if (roomProfile != null) "Room Database" else "Not found in Room"}",
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



                Text("First Name: ${profile?.firstName ?: "N/A"}")
                Text("Last Name: ${profile?.lastName ?: "N/A"}")
                Text("Email: ${profile?.email ?: "N/A"}")
                Text("Bio: ${companyProfile?.bio ?: "N/A"}")
                Text("Status: ${companyProfile?.status ?: "N/A"}")
                Text("Company: ${companyProfile?.companyName ?: "N/A"}")

//                Text("Profiles in database: $profileCount")
                roomProfile?.let {
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