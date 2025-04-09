package com.basecampers.basecamp.tabs.profile

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import com.basecampers.basecamp.tabs.profile.models.UserStatusConverter

@Database(entities = [ProfileModel::class], version = 1, exportSchema = false)
@TypeConverters(UserStatusConverter::class)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: ProfileDatabase? = null

        fun getDatabase(context: Context): ProfileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileDatabase::class.java,
                    "profile_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
