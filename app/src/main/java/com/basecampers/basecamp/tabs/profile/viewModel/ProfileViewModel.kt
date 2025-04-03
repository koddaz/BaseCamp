package com.basecampers.basecamp.tabs.profile.viewModel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

import com.basecampers.basecamp.tabs.profile.ProfileDatabase
import com.basecampers.basecamp.tabs.profile.ProfileRepository
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import kotlinx.coroutines.flow.Flow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProfileRepository
    
    init {
        val profileDao = ProfileDatabase.getDatabase(application).profileDao()
        repository = ProfileRepository(profileDao)
    }
    
    // Observe profile from Room
    fun observeProfile(uid: String): Flow<ProfileModel?> {
        return repository.getProfileByUid(uid)
    }
    
    // Refresh from Firebase
    fun refreshProfile(uid: String) {
        viewModelScope.launch {
            repository.refreshProfileFromFirestore(uid)
        }
    }
    
    // Testing getProfileCount function.
    suspend fun getProfileCount(): Int {
        return repository.getProfileCount()
    }
}