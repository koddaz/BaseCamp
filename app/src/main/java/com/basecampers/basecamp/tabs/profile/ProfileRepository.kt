package com.basecampers.basecamp.tabs.profile

import android.util.Log
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val profileDao: ProfileDao) {

    private val firestore = FirebaseFirestore.getInstance()
    
    fun getProfileByUid(uid: String): Flow<ProfileModel?> {
        return profileDao.getProfileByUid(uid)
    }
    
    //suspend fun getProfileCount(): Int {
     //   return profileDao.getProfileCount()
    //}

    fun getProfileCount(): Flow<Int> {
        return profileDao.getProfileCount() // Access the instance method
    }


    suspend fun refreshProfileFromFirestore(uid: String) {
        try {
            Log.d("ProfileRepo", "Fetching profile for UID: $uid")
            val document = firestore.collection("users").document(uid).get().await()
            
            // Check if document exists
            if (!document.exists()) {
                Log.e("ProfileRepo", "Document does not exist for UID: $uid")
                return
            }
            
            val profile = document.toObject(ProfileModel::class.java)
            Log.d("ProfileRepo", "Fetched profile: $profile")
            
            profile?.let {
                // Ensure ID is set correctly
                val profileWithId = it.copy(id = uid)
                Log.d("ProfileRepo", "Saving profile to Room: $profileWithId")
                profileDao.insertProfile(profileWithId)
            } ?: Log.e("ProfileRepo", "Failed to parse profile for UID: $uid")
        } catch (e: Exception) {
            Log.e("ProfileRepo", "Error fetching profile", e)
        }
    }
    
    
}