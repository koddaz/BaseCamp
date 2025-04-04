package com.basecampers.basecamp.authentication.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    
    val database = Firebase.database.reference
    val firestore = Firebase.firestore
    
    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()
    
    private val _userInfo = MutableStateFlow<ProfileInfo?>(null)
    val userInfo = _userInfo.asStateFlow()
    
    data class ProfileInfo(
        val email: String = "",
        val userName: String = "",
        val imageUrl: String? = null,
        val bio: String = "",
        val status: String = "",
        val companyName: String = ""
    )
    
    init {
        checkLoggedin()
    }
    
    //----------------------------------------------------------------------------
    // AUTHENTICATION FUNCTIONALITY
    //----------------------------------------------------------------------------
    
    fun isLoggedInTrue() {
        _loggedin.value = true
        Log.i("isLoggedInTrueDEBUG", "Logged in = ${loggedin.value}")
    }
    
    fun isLoggedInFalse() {
        _loggedin.value = false
        Log.i("isLoggedInFalseDEBUG", "Logged out = ${loggedin.value}")
    }
    
    fun checkLoggedin() {
        val user = Firebase.auth.currentUser
        _loggedin.value = user != null
        
        if (user != null) {
            // Fetch complete user info
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Map all fields from Firestore document to ProfileInfo
                        val profileInfo = ProfileInfo(
                            email = document.getString("email") ?: "",
                            userName = document.getString("userName") ?: "",
                            imageUrl = document.getString("imageUrl"),
                            bio = document.getString("bio") ?: "",
                            status = document.getString("status") ?: "",
                            companyName = document.getString("companyName") ?: ""
                        )
                        _userInfo.value = profileInfo
                        _loggedin.value = true
                        Log.i("CHECKLOGINDEBUG", "Logged in = ${loggedin.value}")
                    } else {
                        _userInfo.value = null
                        Log.i("CHECKLOGINDEBUG", "User document not found")
                    }
                }
                .addOnFailureListener {
                    _userInfo.value = null
                    _loggedin.value = false
                    Log.e("CHECKLOGINDEBUG", "Failed to fetch user info", it)
                }
        } else {
            _userInfo.value = null
        }
    }
    
    fun downloadProfileInformation() {
        //Här kan man ladda ned hela profilinformatoinen.
    }
    
    // Suggested rename: signInWithEmailAndPassword
    fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checkLoggedin()
            // Ensure profile is complete when logging in
            ensureCompleteUserProfile(Firebase.auth.currentUser?.uid)
            Log.i("LOGINDEBUG", "Checked login")
        }.addOnFailureListener {
            // fel
        }
    }
    
    // Suggested rename: signInTestUser1
    fun loginUser1() {
        val email = "1@hotmail.com"
        val password = "test1234"
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checkLoggedin()
            // Ensure profile is complete when logging in
            ensureCompleteUserProfile(Firebase.auth.currentUser?.uid)
        }.addOnFailureListener {
            // FEL
        }
    }
    
    // Suggested rename: signInTestUser2
    fun loginUser2() {
        val email = "2@hotmail.com"
        val password = "test123"
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checkLoggedin()
            // Ensure profile is complete when logging in
            ensureCompleteUserProfile(Firebase.auth.currentUser?.uid)
        }.addOnFailureListener {
            // FEL
        }
    }
    
    fun forgotPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("BASECAMPDEBUG", "Email Sent to user")
                }
            }
    }
    
    // Suggested rename: createAccount
//    fun register(email: String, password: String) {
//        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
//            checkLoggedin()
//        }.addOnFailureListener {
//            // fel
//        }
//    }
    
    // Suggested rename: createAccountWithProfile
    fun registerAndCreateUserInFirestore(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Basic user data
                    val user = mapOf(
                        "email" to email,
                        "userName" to email.substringBefore("@"),
                        "bio" to "No bio yet",
                        "imageUrl" to "",
                        "status" to "USER",
                        "companyName" to "No company yet"
                    )
                    
                    // Create full profile document right away
                    firestore.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            // Update login state
                            checkLoggedin()
                            // No need to call ensureCompleteUserProfile since we're creating a complete profile
                        }
                        .addOnFailureListener {
                            // Firestore error
                            Log.e("AuthViewModel", "Failed to create user profile in Firestore")
                        }
                }
            }.addOnFailureListener {
                // Auth error
                Log.e("AuthViewModel", "Failed to create user account")
            }
    }
    
    fun logout() {
        Firebase.auth.signOut()
        checkLoggedin()
    }
    
    fun deleteUser() {
        Firebase.auth.currentUser?.delete()?.addOnCompleteListener {
            checkLoggedin()
        }
    }
    
    fun getCurrentUserUid(): String? {
        return Firebase.auth.currentUser?.uid
    }
    
    //----------------------------------------------------------------------------
    // PROFILE FUNCTIONALITY
    //----------------------------------------------------------------------------
    
    // Suggested rename: getUserInfo
    fun fetchUserInfoFromFirestore(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Create a complete ProfileInfo object from the document
                    val profileInfo = ProfileInfo(
                        email = document.getString("email") ?: "",
                        userName = document.getString("userName") ?: "", // Note: using "userName" not "username"
                        imageUrl = document.getString("imageUrl"),
                        bio = document.getString("bio") ?: "",
                        status = document.getString("status") ?: "",
                        companyName = document.getString("companyName") ?: ""
                    )
                    _userInfo.value = profileInfo
                    Log.d("ProfileFetch", "Successfully fetched profile for $userId")
                } else {
                    _userInfo.value = null
                    Log.d("ProfileFetch", "No profile document exists for $userId")
                }
            }
            .addOnFailureListener { e ->
                _userInfo.value = null
                Log.e("ProfileFetch", "Failed to fetch profile", e)
            }
    }
    
    // New function to ensure complete profile on login
    private fun ensureCompleteUserProfile(userId: String?) {
        if (userId == null) return
        
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val updates = mutableMapOf<String, Any>()
                    
                    // Use correct field name (userName)
                    if (!document.contains("userName")) {
                        updates["userName"] = document.getString("email")?.substringBefore("@") ?: "No name yet"
                    }
                    
                    if (!document.contains("bio")) {
                        updates["bio"] = "No bio yet"
                    }
                    
                    if (!document.contains("imageUrl")) {
                        updates["imageUrl"] = ""
                    }
                    
                    if (!document.contains("status")) {
                        updates["status"] = "USER"
                    }
                    
                    if (!document.contains("companyName")) {
                        updates["companyName"] = "No company yet"
                    }
                    
                    // Only update if there are missing fields
                    if (updates.isNotEmpty()) {
                        firestore.collection("users").document(userId).update(updates)
                    }
                }
            }
        
    }
    
    // New function to get complete profile data
    fun getCompleteUserProfile(userId: String, callback: (Map<String, Any>?) -> Unit) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profileData = document.data
                    callback(profileData)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}