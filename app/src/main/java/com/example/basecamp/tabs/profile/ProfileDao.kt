package com.example.basecamp.tabs.profile

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {

    @Query("SELECT * FROM profile_table LIMIT 1")
    fun getProfile(): LiveData<Profile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()
}
