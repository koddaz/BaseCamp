import android.util.Log
import com.example.basecamp.tabs.profile.Profile
import com.example.basecamp.tabs.profile.ProfileDao


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await




class ProfileRepository(private val profileDao: ProfileDao) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun fetchProfileFromFirestore(uid: String): Profile? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            val profile = document.toObject(Profile::class.java)
            profile?.let {
                profileDao.deleteAll()
                profileDao.insertProfile(it)
            }
            profile
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Error fetching profile", e)
            null
        }
    }
}

