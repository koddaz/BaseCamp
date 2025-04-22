package com.basecampers.basecamp.authentication.viewModels

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.aRootFolder.AppPreferences
import com.basecampers.basecamp.aRootFolder.UserSession
import com.basecampers.basecamp.company.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.viewModel.ProfileViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

/**
 * ViewModel handling all authentication-related operations.
 */
class AuthViewModel(private val application: android.app.Application) : AndroidViewModel(application) {
    private val tag = this::class.java.simpleName
    
    private val appPreferences = AppPreferences(application)

    // General state values
    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    // SOCIAL tab user state
    private val _isSuper = MutableStateFlow(false)
    val isSuper: StateFlow<Boolean> = _isSuper.asStateFlow()

    private val _companyProfile = MutableStateFlow<CompanyModel?>(null)
    val companyProfile = _companyProfile.asStateFlow()

    // Validation state values
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
            RegisterErrors.EMAIL_NOT_VALID,
            RegisterErrors.EMAIL_ALREADY_IN_USE
        ) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    
    // Error enums
    sealed interface AuthError {
        val message: String
    }

    enum class RegisterErrors : AuthError {
        PASSWORD_EMPTY,
        PASSWORD_TOO_SHORT,
        PASSWORD_NO_SPECIAL_CHAR,
        PASSWORD_NO_UPPERCASE,
        PASSWORD_NO_NUMBER,
        CONFIRM_PASSWORD_EMPTY,
        CONFIRM_PASSWORD_MISMATCH,
        EMAIL_EMPTY,
        EMAIL_NOT_VALID,
        EMAIL_ALREADY_IN_USE;

        override val message: String
            get() = when (this) {
                PASSWORD_EMPTY -> "Password cannot be empty"
                PASSWORD_TOO_SHORT -> "Password must be at least 6 characters long"
                PASSWORD_NO_SPECIAL_CHAR -> "Password must contain at least one special character"
                PASSWORD_NO_UPPERCASE -> "Password must contain at least one uppercase letter"
                PASSWORD_NO_NUMBER -> "Password must contain at least one number"
                CONFIRM_PASSWORD_EMPTY -> "Confirm password cannot be empty"
                CONFIRM_PASSWORD_MISMATCH -> "Passwords do not match"
                EMAIL_EMPTY -> "Email cannot be empty"
                EMAIL_NOT_VALID -> "Email is not valid"
                EMAIL_ALREADY_IN_USE -> "This email is already registered"
            }
    }
    
    enum class LoginErrors : AuthError {
        EMAIL_NOT_VALID,
        PASSWORD_NOT_VALID;

        override val message: String
            get() = when (this) {
                EMAIL_NOT_VALID -> "Email not found or invalid"
                PASSWORD_NOT_VALID -> "Password is incorrect"
            }
    }

    init {
        checkLoggedin()
    }

    //=== AUTH FUNCTIONS ===//
    
    /**
     * Checks if a user is currently logged in and updates UserSession.
     */
    fun checkLoggedin() {
        val user = Firebase.auth.currentUser
        _loggedin.value = user != null
        
        if (user != null) {
            // Initialize UserSession with user ID
            UserSession.setUserId(user.uid)
        } else {
            UserSession.clearSession()
        }
    }
    
    /**
     * Handles user login with email and password.
     * Updates UserSession on successful authentication.
     */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            // Get user ID
            val userId = authResult.user?.uid ?: return@addOnSuccessListener
            
            // Initialize UserSession with user ID
            UserSession.setUserId(userId)
            
            // Use existing functions
            checkLoggedin()
            clearLoginErrors()
            
            Log.i("LOGINDEBUG", "Checked login")
            onSuccess()
        }.addOnFailureListener { exception ->
            // Existing error handling
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
            onError()
        }
    }
    
    /**
     * Logs the user out and clears UserSession.
     */
    fun logout() {
        Firebase.auth.signOut()
        // Clear UserSession
        UserSession.clearSession()
        // Clear selected company
        appPreferences.clearSelectedCompanyId()
        // Update existing state
        checkLoggedin()
    }

    /**
     * Deletes the current user's account from Firebase Auth and removes their data from
     * the Firestore 'users' collection and 'users' subcollections under 'companies'.
     */
    suspend fun deleteUser() {
        val user = Firebase.auth.currentUser
        val userId = user?.uid ?: return
        val db = Firebase.firestore

        try {
            // Start a firebase batch operation
            val batch = db.batch()

            // Get user company list
            val companyList = UserSession.profile.value?.companyList
                ?: db.collection("users").document(userId).get().await()
                    .get("companyList") as? List<String>
                ?: emptyList()

            // Remove user from companies' users sub-collections
            deleteCompanyProfiles(userId, companyList, batch)

            // Delete user's profile
            deleteProfile(userId, batch)

            // Commit all firestore deletions
            batch.commit().await()

            // Delete firebase auth account
            user.delete().await()

            // Clear UserSession
            UserSession.clearSession()
            // Clear selected company
            appPreferences.clearSelectedCompanyId()
            // Update existing state
            checkLoggedin()

            Log.d("RemoveUser", "User $userId successfully deleted from Firebase Auth and Firestore")
        } catch (e: Exception) {
            Log.e("RemoveUser", "Failed to delete user data: ${e.message}", e)
            // In a production environment, we might want to handle partial deletions
            // and implement retry logic or cleanup mechanisms
        }
    }

    /**
     * Removes the users company profile from the 'users' subcollection of each company.
     */
    private fun deleteCompanyProfiles(
        userId: String,
        companyList: List<String>,
        batch: com.google.firebase.firestore.WriteBatch
    ) {
        val db = Firebase.firestore
        companyList.forEach { companyId ->
            val companyUserRef = db.collection("companies")
                .document(companyId)
                .collection("users")
                .document(userId)
            batch.delete(companyUserRef)
        }
        Log.d(tag, "Scheduled deletion of user $userId from companies' users subcollections")
    }

    /**
     * Deletes the user's profile from the 'users' collection in Firestore.
     */
    private fun deleteProfile(
        userId: String,
        batch: com.google.firebase.firestore.WriteBatch
    ) {
        val db = Firebase.firestore
        val userProfileRef = db.collection("users").document(userId)
        batch.delete(userProfileRef)
        Log.d(tag, "Scheduled deletion of profile for user $userId")
    }

    /**
     * Sends a password reset email to the specified address.
     */
    fun forgotPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("BASECAMPDEBUG", "Email Sent to user")
                }
            }
    }
    
    /**
     * Returns the current user's UID.
     */
    fun getCurrentUserUid(): String? {
        return Firebase.auth.currentUser?.uid
    }
    
    /**
     * Creates a new user account with the provided information.
     * Also creates a basic profile using ProfileViewModel.
     */
    fun registerAsUser(
        email: String,
        password: String,
        confirmPassword: String,
        profileViewModel: ProfileViewModel,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val checkError = validateAll(email, password, confirmPassword)
        
        if(checkError.isEmpty()) {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { authResult ->
                    val userId = authResult.user?.uid
                    if (userId != null) {
                        // Use ProfileViewModel to create the profile
                        profileViewModel.createProfile(
                            email = email,
                            userId = userId,
                            onSuccess = {
                                // Update login state
                                checkLoggedin()
                                onSuccess()
                            },
                            onError = { error ->
                                Log.e("AuthViewModel", "Failed to create profile: $error")
                                onError(error)
                            }
                        )
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AuthViewModel", "Failed to create user account: ${e.message}")
                    if (e.message?.contains("email address is already in use") == true) {
                        _registerErrorMessage.value = listOf(RegisterErrors.EMAIL_ALREADY_IN_USE)
                        _emailValid.value = false
                    } else {
                        _registerErrorMessage.value = listOf(RegisterErrors.EMAIL_NOT_VALID)
                    }
                    onError(e.message ?: "Registration failed")
                }
        } else {
            onError("Validation failed")
        }
    }
    
    //=== TEST LOGIN FUNCTIONS ===//
    
    /**
     * Test function: Logs in an Admin user account.
     */
    fun loginUser1() {
        val email = "user@one.test"
        val password = "aQ!2345"
        login(email, password)
    }
    
    /**
     * Test function: Logs in a super user account.
     */
    fun loginUser2() {
        val email = "user@two.test"
        val password = "aQ!2345"
        login(email, password)
    }
    
    /**
     * Test function: Logs in a user account.
     */
    fun loginUser3() {
        val email = "user@three.test"
        val password = "aQ!2345"
        login(email, password)
    }
    /*
    *//**
     * Test function: Toggles super user status for testing in SOCIAL tab.
     *//*
    fun toggleSuperUser() {
        _isSuper.value = !_isSuper.value
    }
    */
    //=== VALIDATION FUNCTIONS ===//
    
    /**
     * Sets UI state for whether email is valid.
     */
    fun validateEmailLive(email: String) {
        _emailValid.value = validateEmail(email).isEmpty()
    }

    /**
     * Sets UI state for whether password is valid.
     */
    fun validatePasswordLive(password: String) {
        _passwordValid.value = validatePassword(password).isEmpty()
    }

    /**
     * Sets UI state for whether confirmation password is valid.
     */
    fun validateConfirmPasswordLive(password: String, confirmPassword: String) {
        _confirmPasswordValid.value = validateConfirmPassword(password, confirmPassword).isEmpty()
    }

    /**
     * Validates all registration fields and returns a list of errors.
     */
    fun validateAll(email: String, password: String, confirmPassword: String) : List<RegisterErrors> {
        val checkError = mutableListOf<RegisterErrors>().apply {
            addAll(validateEmail(email))
            addAll(validatePassword(password))
            addAll(validateConfirmPassword(password, confirmPassword))
        }
        _registerErrorMessage.value = checkError
        return checkError
    }

    /**
     * Validates password format and returns a list of errors.
     */
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

    /**
     * Validates confirmation password match and returns a list of errors.
     */
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

    /**
     * Validates email format and returns a list of errors.
     */
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

    /**
     * Clears login error messages.
     */
    fun clearLoginErrors() {
        _loginErrorMessage.value = emptyList()
    }

    /**
     * Clears password validation error messages.
     */
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

    /**
     * Clears confirm password validation error messages.
     */
    fun clearConfirmPasswordErrors() {
        val currentErrors = registerErrorMessage.value.toMutableList()
        currentErrors.removeAll { it in listOf(
            RegisterErrors.CONFIRM_PASSWORD_EMPTY,
            RegisterErrors.CONFIRM_PASSWORD_MISMATCH
        )}
        _registerErrorMessage.value = currentErrors
    }

    /**
     * Clears email validation error messages.
     */
    fun clearEmailErrors() {
        val currentErrors = registerErrorMessage.value.toMutableList()
        currentErrors.removeAll { it in listOf(
            RegisterErrors.EMAIL_EMPTY,
            RegisterErrors.EMAIL_NOT_VALID,
            RegisterErrors.EMAIL_ALREADY_IN_USE
        )}
        _registerErrorMessage.value = currentErrors
    }

    /**
     * Checks if the provided email has a valid format.
     */
    fun isEmailValid(email: String): Boolean {
        // Basic pattern check
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        // Additional TLD validation. TLD = Top-level domain
        val tld = email.substringAfterLast(".")
        return tld.length >= 2
    }

    /**
     * Sets logged in state to true.
     */
    fun isLoggedInTrue() {
        _loggedin.value = true
        Log.i("isLoggedInTrueDEBUG", "Logged in = ${loggedin.value}")
    }

    /**
     * Sets logged in state to false.
     */
    fun isLoggedInFalse() {
        _loggedin.value = false
        Log.i("isLoggedInFalseDEBUG", "Logged out = ${loggedin.value}")
    }
}