/*
package com.basecampers.basecamp.navigation.models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.basecampers.basecamp.tabs.profile.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.profile.models.UserStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URL

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<CompanyProfileModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    val database = Firebase.database.reference
    val firestore = Firebase.firestore

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    private val _userInfo = MutableStateFlow<Pair<String?, String?>>(Pair(null, null))
    val userInfo = _userInfo.asStateFlow()

    private val _userStatus = MutableStateFlow(CompanyProfileModel(status = UserStatus.USER))
    val userStatus : StateFlow<CompanyProfileModel> = _userStatus

    init {
        checklogin()
    }

    fun isLoggedInTrue() {
        _loggedin.value = true
        Log.i("isLoggedInTrueDEBUG", "Logged in = ${loggedin.value}")
    }

    fun isLoggedInFalse() {
        _loggedin.value = false
        Log.i("isLoggedInFalseDEBUG", "Logged out = ${loggedin.value}")
    }

    fun checklogin(companyId: CompanyProfileModel? = null) {
        val user = Firebase.auth.currentUser
        _loggedin.value = user != null

        if (user != null) {
            fetchUserInfoFromFirestore(userId = user.uid, companyId = companyId?.companyId ?: "")
            if (Firebase.auth.currentUser == null) {
                _loggedin.value = false
                Log.i("CHECKLOGINDEBUG", "Logged in = ${loggedin.value}")
            } else {
                _userInfo.value = Pair(null, null)
                _loggedin.value = true
                Log.i("CHECKLOGINDEBUG", "Logged in = ${loggedin.value}")
            }
        }
    }
        fun login(email: String, password: String) {
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                checklogin()
                fetchCurrentUserModel()
                Log.i("LOGINDEBUG", "Checked login")
            }.addOnFailureListener {
                // fel
            }
        }

        fun loginUser1() {
            val email = "1@hotmail.com"
            val password = "test1234"
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                checklogin()
            }.addOnFailureListener {
                // FEL
            }
        }

        fun loginUser2() {
            val email = "2@hotmail.com"
            val password = "test123"
            Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                checklogin()
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



    fun setStatus(newStatus: UserStatus) {
        _userStatus.value = _userStatus.value.copy(status = newStatus)
    }

    fun registerAsCompany(
        email: String,
        password: String,
        companyName: String,
        firstName: String,
        lastName: String
    ) {
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {

                    val companyInfo = CompanyModel(
                        companyName = companyName,
                        ownerUID = userId,
                        bio = "",
                        imageUrl = null // ADD IMAGE
                    )

                    val companyAdmin = CompanyProfileModel(
                        imageUrl = null, // ADD IMAGE!
                        bio = "",
                        status = UserStatus.ADMIN,
                        id = userId,
                    )
                    val userRef =
                        firestore.collection("companies").document(companyAdmin.companyId)
                            .collection("users").document(userId)

                    val companyRef =
                        firestore.collection("companies").document(companyInfo.companyName)

                    companyRef.set(companyRef).addOnSuccessListener {
                        Log.d("AuthViewModel", "Company created: $companyInfo")
                    }

                    userRef.set(companyAdmin).addOnSuccessListener {
                        checklogin()
                        setStatus(companyAdmin.status)
                    }


                }
            }
    }

    fun registerAsUser(email: String, password: String, firstName: String, lastName: String, companyName: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                val user = CompanyProfileModel(
                    imageUrl = null, // ADD IMAGE!
                    bio = "",
                    status = UserStatus.USER,
                    id = userId,
                )
                val userRef = firestore
                    .collection("companies")
                    .document(user.companyId)
                    .collection("users").document(userId)
                userRef.set(user).addOnSuccessListener {
                    checklogin()
                    setStatus(UserStatus.USER)
                }
            }
        }
    }
        fun registerAndCreateUserInFirestore(email: String, password: String) {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid
                    if (userId != null) {
                        val user = mapOf(
                            "email" to email,
                            "username" to email.substringBefore("@") // user before at sign
                        )

                        firestore.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                checklogin()
                            }
                            .addOnFailureListener {
                                //  Firestore fel
                            }
                    }
                }.addOnFailureListener {
                //  Auth fel
            }
        }

        fun logout() {
            Firebase.auth.signOut()
            checklogin()
        }

        fun deleteUser() {
            Firebase.auth.currentUser?.delete()?.addOnCompleteListener {
                checklogin()
            }
        }

    fun fetchCurrentUserModel() {
        val userId = getCurrentUserUid() ?: return
        Log.d("AuthViewModel", "Fetching user model for ID: $userId")

        // Try direct company path first
        firestore.collection("companies")
            .get()
            .addOnSuccessListener { companySnapshots ->
                var userFound = false

                for (companyDoc in companySnapshots.documents) {
                    val companyId = companyDoc.id
                    Log.d("AuthViewModel", "Checking company: $companyId")

                    firestore.collection("companies")
                        .document(companyId)
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc.exists()) {
                                userFound = true
                                Log.d("AuthViewModel", "Found user in company: $companyId")

                                try {
                                    val email = userDoc.getString("email") ?: ""
                                    val name = userDoc.getString("name") ?: "No name yet"
                                    val imageUrlString = userDoc.getString("imageUrl")
                                    val imageUrl = if (!imageUrlString.isNullOrEmpty()) {
                                        try { URL(imageUrlString) } catch (e: Exception) { null }
                                    } else null
                                    val bio = userDoc.getString("bio") ?: "No bio yet"
                                    val statusString = userDoc.getString("status")

                                    // Determine user status
                                    val status = when {
                                        userDoc.getBoolean("isAdmin") == true -> UserStatus.ADMIN
                                        statusString == "SUPER_USER" -> UserStatus.SUPER_USER
                                        else -> UserStatus.USER
                                    }

                                    val companyProfileModel = CompanyProfileModel(
                                        imageUrl = imageUrl,
                                        bio = bio,
                                        status = status,
                                        id = userId,
                                    )

                                    _currentUser.value = companyProfileModel
                                    Log.d("AuthViewModel", "User model created: $companyProfileModel")
                                } catch (e: Exception) {
                                    Log.e("AuthViewModel", "Error creating UserModel", e)
                                }
                            }
                        }
                }

                if (!userFound) {
                    Log.e("AuthViewModel", "User not found in any company")
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to fetch companies", e)
            }
    }
        fun getCurrentUserUid(): String? {
            return Firebase.auth.currentUser?.uid
        }


        fun fetchUserInfoFromFirestore(userId: String, companyId: String) {
            firestore.collection("companies").document(companyId).collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username")
                        val email = document.getString("email")
                        _userInfo.value = Pair(username, email)
                    }
                }
                .addOnFailureListener {
                    _userInfo.value = Pair(null, null) // firestore fel
                }
        }
    }




sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

data class User(
    val uid: String = "",
    val email: String = "",
    @field:JvmField // This makes the field visible to Firestore
    val isAdmin: Boolean = false,
    val name: String = "",
    val companyId: String = ""
)


data class Company(
    val id: String = "",
    val name: String = "",
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis()
)*/
