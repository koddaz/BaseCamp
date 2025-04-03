package com.basecampers.basecamp.tabs.profile

import androidx.room.*
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    
    @Query("SELECT * FROM profile_table WHERE id = :uid")
    fun getProfileByUid(uid: String): Flow<ProfileModel?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileModel)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM profile_table")
    suspend fun getProfileCount(): Int
}