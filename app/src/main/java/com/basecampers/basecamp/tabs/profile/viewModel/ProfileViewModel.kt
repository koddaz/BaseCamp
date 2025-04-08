package com.basecampers.basecamp.tabs.profile.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.basecampers.basecamp.tabs.profile.ProfileDao
import kotlinx.coroutines.launch

import com.basecampers.basecamp.tabs.profile.ProfileDatabase
import com.basecampers.basecamp.tabs.profile.ProfileRepository
import com.basecampers.basecamp.tabs.profile.models.ProfileModel


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.invoke

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProfileRepository
    val profileCount: StateFlow<Int>
    init {
        val profileDao = ProfileDatabase.getDatabase(application).profileDao()
        repository = ProfileRepository(profileDao)

        profileCount = repository.getProfileCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0) // Set default value 0



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

    //suspend fun getProfileCount(): Int {
      //  return repository.getProfileCount()
    //}

    //Testing getProfileCount function.
    fun getProfileCount():Int{
        return profileCount.value
    }



}