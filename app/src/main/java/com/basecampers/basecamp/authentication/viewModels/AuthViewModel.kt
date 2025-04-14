package com.basecampers.basecamp.authentication.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.UserModel
import com.basecampers.basecamp.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.URL

class AuthViewModel : ViewModel() {
    
    private val tag = this::class.java.simpleName
    
    val database = Firebase.database.reference
    val firestore = Firebase.firestore
    
    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()
    
    private val _userInfo = MutableStateFlow<ProfileInfo?>(null)
    val userInfo = _userInfo.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser = _currentUser.asStateFlow()
    
    //VALIDATION
    private val _registerErrorMessage = MutableStateFlow(listOf<RegisterErrors>())
    val registerErrorMessage = _registerErrorMessage.asStateFlow()
    
    private val _loginErrorMessage = MutableStateFlow(listOf<LoginErrors>())
    val loginErrorMessage = _loginErrorMessage.asStateFlow()
    
    private val _emailValid = MutableStateFlow(false)
    val emailValid = _emailValid.asStateFlow()
    
    private val _passwordValid = MutableStateFlow(false)
    val passwordValid = _passwordValid.asStateFlow()
    
    private val _confirmPasswordValid = MutableStateFlow(false)
    val confirmPasswordValid = _confirmPasswordValid.asStateFlow()
    
    val hasEmailError = registerErrorMessage.map { errors ->
        errors.any { it in listOf(
            RegisterErrors.EMAIL_EMPTY,
            RegisterErrors.EMAIL_NOT_VALID)
        } }.stateIn(viewModelScope, SharingStarted.Lazily, false)
    
    var errorMessages = mapOf(
        RegisterErrors.EMAIL_EMPTY to "Email cannot be empty",
        RegisterErrors.EMAIL_NOT_VALID to "Email is not valid",
        RegisterErrors.PASSWORD_EMPTY to "Password cannot be empty",
        RegisterErrors.PASSWORD_TOO_SHORT to "Password must be at least 6 characters long",
        RegisterErrors.PASSWORD_NO_SPECIAL_CHAR to "Password must contain at least one special character",
        RegisterErrors.PASSWORD_NO_UPPERCASE to "Password must contain at least one uppercase letter",
        RegisterErrors.PASSWORD_NO_NUMBER to "Password must contain at least one number",
        RegisterErrors.CONFIRM_PASSWORD_EMPTY to "Confirm password cannot be empty",
        RegisterErrors.CONFIRM_PASSWORD_MISMATCH to "Passwords do not match",
        LoginErrors.EMAIL_NOT_VALID to "Email not found or invalid",
        LoginErrors.PASSWORD_NOT_VALID to "Password is incorrect"
    )
    
    enum class RegisterErrors {
        PASSWORD_EMPTY,
        PASSWORD_TOO_SHORT,
        PASSWORD_NO_SPECIAL_CHAR,
        PASSWORD_NO_UPPERCASE,
        PASSWORD_NO_NUMBER,
        CONFIRM_PASSWORD_EMPTY,
        CONFIRM_PASSWORD_MISMATCH,
        EMAIL_EMPTY,
        EMAIL_NOT_VALID
    }
    
    enum class LoginErrors {
        EMAIL_NOT_VALID,
        PASSWORD_NOT_VALID
    }
    
    fun validateEmailLive(email: String) {
        _emailValid.value = validateEmail(email).isEmpty()
    }
    
    fun validatePasswordLive(password: String) {
        _passwordValid.value = validatePassword(password).isEmpty()
    }
    
    fun validateConfirmPasswordLive(password: String, confirmPassword: String) {
        _confirmPasswordValid.value = validateConfirmPassword(password, confirmPassword).isEmpty()
    }

    fun validateAll(email: String, password: String, confirmPassword: String) : List<RegisterErrors> {
        val checkError = mutableListOf<RegisterErrors>().apply {
            addAll(validateEmail(email))
            addAll(validatePassword(password))
            addAll(validateConfirmPassword(password, confirmPassword))
        }
        _registerErrorMessage.value = checkError
        return checkError
    }
    
    fun validatePassword(password: String) : List<RegisterErrors> {
        val checkError = mutableListOf<RegisterErrors>()
        val specialCharPattern = Regex("[!@#\$%^&*()\\-+=\\[\\]{}|;:,.<>?/]")
        val uppercaseRegex = Regex("[A-Z]")
        val digitRegex = Regex("[0-9]")
        
        if(password.isEmpty()) {
            checkError.add(RegisterErrors.PASSWORD_EMPTY)
        }
        if(password.length < 6) {
            checkError.add(RegisterErrors.PASSWORD_TOO_SHORT)
        }
        if(!password.contains((specialCharPattern))) {
            checkError.add(RegisterErrors.PASSWORD_NO_SPECIAL_CHAR)
        }
        if(!password.contains(uppercaseRegex)) {
            checkError.add(RegisterErrors.PASSWORD_NO_UPPERCASE)
        }
        if(!password.contains(digitRegex)) {
            checkError.add(RegisterErrors.PASSWORD_NO_NUMBER)
        }
        return checkError
    }
    
    fun validateConfirmPassword(password: String, confirmPassword: String) : List<RegisterErrors> {
        val checkError = mutableListOf<RegisterErrors>()
        
        if(confirmPassword != password) {
            checkError.add(RegisterErrors.CONFIRM_PASSWORD_MISMATCH)
        }
        if(confirmPassword.isEmpty()) {
            checkError.add(RegisterErrors.CONFIRM_PASSWORD_EMPTY)
        }
        return checkError
    }
    
    fun validateEmail(email: String) : List<RegisterErrors> {
        val checkError = mutableListOf<RegisterErrors>()
        
        if(email.isEmpty()) {
            checkError.add(RegisterErrors.EMAIL_EMPTY)
        }
        if(!isEmailValid(email)) {
            checkError.add(RegisterErrors.EMAIL_NOT_VALID)
        }
        return checkError
    }
    
    fun clearLoginErrors() {
        _loginErrorMessage.value = emptyList()
    }
    
    fun clearPasswordErrors() {
        val currentErrors = registerErrorMessage.value.toMutableList()
        currentErrors.removeAll { it in listOf(
            RegisterErrors.PASSWORD_EMPTY,
            RegisterErrors.PASSWORD_TOO_SHORT,
            RegisterErrors.PASSWORD_NO_SPECIAL_CHAR,
            RegisterErrors.PASSWORD_NO_UPPERCASE,
            RegisterErrors.PASSWORD_NO_NUMBER,
        )}
        _registerErrorMessage.value = currentErrors
    }
    
    fun clearConfirmPasswordErrors() {
        val currentErrors = registerErrorMessage.value.toMutableList()
        currentErrors.removeAll { it in listOf(
            RegisterErrors.CONFIRM_PASSWORD_EMPTY,
            RegisterErrors.CONFIRM_PASSWORD_MISMATCH
        )}
        _registerErrorMessage.value = currentErrors
    }
    
    fun clearEmailErrors() {
        val currentErrors = registerErrorMessage.value.toMutableList()
        currentErrors.removeAll { it in listOf(
            RegisterErrors.EMAIL_EMPTY,
            RegisterErrors.EMAIL_NOT_VALID
        )}
        _registerErrorMessage.value = currentErrors
    }
    
    fun isEmailValid(email: String): Boolean {
        // Basic pattern check
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        // Additional TLD validation. TLD = Top-level domain
        val tld = email.substringAfterLast(".")
        return tld.length >= 2
    }
    //VALIDATION
    
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
    
    // TAGET FRÅN DEN ANDRA!!!
    fun register(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            checkLoggedin()
        }.addOnFailureListener {
            // fel
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
    
    //TAGET FRÅN DEN ANDRA!!!
    
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
            clearLoginErrors()
            ensureCompleteUserProfile(Firebase.auth.currentUser?.uid)
            Log.i("LOGINDEBUG", "Checked login")
        }.addOnFailureListener { exception ->
            val errors = mutableListOf<LoginErrors>()
            Log.d(tag, "Firebase error message: ${exception.message}")
            when (exception.message) {
                "The email address is badly formatted." -> {
                    // Only email is invalid due to syntax
                    errors.add(LoginErrors.EMAIL_NOT_VALID)
                    errors.add(LoginErrors.PASSWORD_NOT_VALID)
                    Log.d(tag, "Added EMAIL_NOT_VALID for badly formatted email")
                }
                else -> {
                    if (!email.contains("@")) {
                        errors.add(LoginErrors.EMAIL_NOT_VALID)
                        errors.add(LoginErrors.PASSWORD_NOT_VALID)
                        Log.d(tag, "Added EMAIL_NOT_VALID for badly formatted email")
                    } else {
                        // All other cases (wrong password, non-existent email, etc.)
                        errors.add(LoginErrors.EMAIL_NOT_VALID)
                        errors.add(LoginErrors.PASSWORD_NOT_VALID)
                        Log.d(tag, "Added EMAIL_NOT_VALID and PASSWORD_NOT_VALID for invalid email or password")
                    }
                }
            }
            Log.d(tag, "Final errors list before setting value: $errors")
            _loginErrorMessage.value = errors
            println(tag + "Login failed ${exception.message}")
        }
    }
    
    // This user will be a normal User
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
    
    // This user will be a SuperUser
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
    
    // This user will be an Admin
    fun loginUser3() {
        val email = "3@hotmail.com"
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
    
    // Suggested rename: createAccountWithProfile
    fun registerAndCreateUserInFirestore(email: String, password: String, confirmPassword: String, companyName : String?) {
        
        val checkError = validateAll(email, password, confirmPassword)
        
        if(checkError.isEmpty()) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
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
                    firestore.collection("users").document(userId).set(user).addOnSuccessListener {
                        // Update login state
                        checkLoggedin()
                        // No need to call ensureCompleteUserProfile since we're creating a complete profile
                    }.addOnFailureListener {
                        // Firestore error
                        Log.e("AuthViewModel", "Failed to create user profile in Firestore")
                    }
                }
            }.addOnFailureListener {
                // Auth error
                Log.e("AuthViewModel", "Failed to create user account")
            }
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


//Taget från den andra AuthViewModel...
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
//Tagit alla models från den andra AuthViewModel också....
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