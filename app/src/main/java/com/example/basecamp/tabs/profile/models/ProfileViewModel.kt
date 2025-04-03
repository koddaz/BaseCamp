package com.example.basecamp.tabs.profile.models

import ProfileRepository
import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch

import com.example.basecamp.tabs.profile.Profile
import com.example.basecamp.tabs.profile.ProfileDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow



class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProfileRepository

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    init {
        val profileDao = ProfileDatabase.getDatabase(application).profileDao()
        repository = ProfileRepository(profileDao)
    }

    fun refreshProfile(uid: String) {
        viewModelScope.launch {
            val fetchedProfile = repository.fetchProfileFromFirestore(uid)
            _profile.value = fetchedProfile
        }
    }


}
