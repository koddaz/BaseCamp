package com.example.basecamp.navigation.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basecamp.UserModel
import com.example.basecamp.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.URL
import kotlin.text.get

class AuthViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()

    val database = Firebase.database.reference
    val firestore = Firebase.firestore

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    private val _userInfo = MutableStateFlow<Pair<String?, String?>>(Pair(null, null))
    val userInfo = _userInfo.asStateFlow()

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

    fun checklogin() {
        val user = Firebase.auth.currentUser
        _loggedin.value = user != null

        if (user != null) {
            fetchUserInfoFromFirestore(user.uid)
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

        fun register(email: String, password: String) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                checklogin()
            }.addOnFailureListener {
                // fel
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

                                    val userModel = UserModel(
                                        email = email,
                                        name = name,
                                        imageUrl = imageUrl,
                                        bio = bio,
                                        status = status,
                                        id = userId,
                                        companyName = companyId
                                    )

                                    _currentUser.value = userModel
                                    Log.d("AuthViewModel", "User model created: $userModel")
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


        fun fetchUserInfoFromFirestore(userId: String) {
            firestore.collection("users").document(userId).get()
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

// DENNA KOLLAR OM USER ÄR ADMIN ELLER INTE! Kanske lägga till en nivå mellan 1-3 admin nivåer
class USERVIEWMODEL() : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies

    init {
        fetchCompanies()
        checkCurrentUserStatus()
    }
    
    fun updateUserStatus(user: User) {
        _isAdmin.value = user.isAdmin
    }
    // Fetch all available companies
    fun fetchCompanies() {
        viewModelScope.launch {
            try {
                firestore.collection("companies")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val companyList = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(Company::class.java)?.copy(id = doc.id)
                        }
                        _companies.value = companyList
                    }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to fetch companies"
            }
        }
    }
    
    fun checkCurrentUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    Log.d("AuthViewModel", "Checking status for user: ${currentUser.uid}")
                    // Get user document from Firestore
                    val userDoc = firestore.collection("users")
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        Log.d("AuthViewModel", "User doc data: ${userDoc.data}")
                        // Try direct access to the field first
                        val adminField = userDoc.getBoolean("isAdmin")
                        Log.d("AuthViewModel", "Admin field direct: $adminField")

                        val userData = userDoc.toObject(User::class.java)
                        Log.d("AuthViewModel", "User object: $userData, isAdmin: ${userData?.isAdmin}")

                        _isAdmin.value = userData?.isAdmin ?: false
                        Log.d("AuthViewModel", "Set isAdmin to: ${_isAdmin.value}")

                        // Update auth state too
                        if (userData != null) {
                            _authState.value = AuthState.Success(userData)
                        }
                    } else {
                        Log.d("AuthViewModel", "User document doesn't exist")
                        _isAdmin.value = false
                    }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error checking admin status", e)
                    _errorMessage.value = "Failed to check admin status: ${e.message}"
                    _isAdmin.value = false
                }
            }
        } else {
            _isAdmin.value = false
        }
    }
}

class atuhSomethingModel : ViewModel() {
    val adminModel = USERVIEWMODEL()
    
    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAdmin = MutableStateFlow<Boolean?>(null)
    val isAdmin: StateFlow<Boolean?> = _isAdmin

    
   
    // In AuthViewModel.kt, add this function
    fun logoutUser() {
        auth.signOut()
        _authState.value = AuthState.Idle
        _isAdmin.value = false // Reset admin status on logout
    }

    fun addSuperUser(user: User) {

    }

    // Modify your checkCurrentUserStatus function to be public and explicitly callable
    

    // Add this method to update admin status after successful login/registration
    

    

    // Register company (admin only)
    fun registerCompany(companyName: String, adminEmail: String, adminPassword: String, adminName: String) {
        
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Create admin user
                val authResult = auth.createUserWithEmailAndPassword(adminEmail, adminPassword).await()
                val adminId = authResult.user?.uid ?: return@launch
                val companyId = companyName.lowercase().replace(Regex("[^a-z0-9]"), "_")


                val company = Company(
                    id = companyId,
                    name = companyName,
                    createdBy = adminId
                )

                firestore.collection("companies")
                    .document(companyId)
                    .set(company)
                    .await()

                // Create admin user under company
                val adminUser = User(
                    uid = adminId,
                    email = adminEmail,
                    isAdmin = true,
                    name = adminName,
                    companyId = companyId
                )

                firestore.collection("companies")
                    .document(companyId)
                    .collection("users")
                    .document(adminId)
                    .set(adminUser)
                    .await()


                _authState.value = AuthState.Success(adminUser)
                adminModel.updateUserStatus(adminUser)
                adminModel.fetchCompanies()

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Company registration failed"
                _authState.value = AuthState.Error(e.message ?: "Company registration failed")
            }
        }
    }


    // In AuthViewModel.kt, make this change:
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Sign in with Firebase Auth
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: return@launch


                // Get user document
                val userDoc = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    // Update isAdmin value immediately before updating auth state
                    _isAdmin.value = user.isAdmin
                    _authState.value = AuthState.Success(user)
                } else {
                    _authState.value = AuthState.Error("User data not found")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    // Register regular user
    fun registerUser(email: String, password: String, name: String, companyId: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Create user auth
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid ?: return@launch

                // Create user document
                val user = User(
                    uid = userId,
                    email = email,
                    isAdmin = false,
                    name = name,
                    companyId = companyId
                )

                // Store directly in company's users collection (same place as admins)
                firestore.collection("companies")
                    .document(companyId)
                    .collection("users")
                    .document(userId)
                    .set(user)
                    .await()

                // Also store in users collection for easy queries
                firestore.collection("users")
                    .document(userId)
                    .set(user)
                    .await()

                _authState.value = AuthState.Success(user)
                adminModel.updateUserStatus(user)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Registration failed"
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
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
)