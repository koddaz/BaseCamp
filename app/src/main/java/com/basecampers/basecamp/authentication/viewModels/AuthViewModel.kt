package com.basecampers.basecamp.authentication.viewModels

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.basecampers.basecamp.tabs.profile.models.CompanyModel
import com.basecampers.basecamp.tabs.profile.models.CompanyProfileModel
import com.basecampers.basecamp.tabs.profile.models.ProfileModel
import com.basecampers.basecamp.tabs.profile.models.UserStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URL

class AuthViewModel : ViewModel() {

    private val tag = this::class.java.simpleName

    val database = Firebase.database.reference
    val firestore = Firebase.firestore

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    private val _profile = MutableStateFlow<ProfileModel?>(null)
    val profile = _profile.asStateFlow()


    private val _companyProfile = MutableStateFlow<CompanyProfileModel?>(null)
    val companyProfile = _companyProfile.asStateFlow()

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

    init {
        checkLoggedin()
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
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false
        }
        // Additional TLD validation. TLD = Top-level domain
        val tld = email.substringAfterLast(".")
        return tld.length >= 2
    }

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
                        val profileModel = ProfileModel(
                            email = document.getString("email") ?: "",
                        )
                        _profile.value = profileModel
                        _loggedin.value = true
                        Log.i("CHECKLOGINDEBUG", "Logged in = ${loggedin.value}")
                    } else {
                        _profile.value = null
                        Log.i("CHECKLOGINDEBUG", "User document not found")
                    }
                }
                .addOnFailureListener {
                    _profile.value = null
                    _loggedin.value = false
                    Log.e("CHECKLOGINDEBUG", "Failed to fetch user info", it)
                }
        } else {
            _profile.value = null
        }
    }



    private fun searchUserInCompanies(userId: String, ) {
        firestore.collection("companies")
            .get()
            .addOnSuccessListener { companySnap ->
                var userFound = false

                if (companySnap.isEmpty) {
                    Log.e("AuthViewModel", "No companies found")
                    return@addOnSuccessListener
                }
                for (companyDoc in companySnap.documents) {
                    val companyId = companyDoc.id
                    Log.d("AuthViewModel", "Checking company: $companyId")
                    firestore.collection("companies")
                        .document(companyId)
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener {
                            userSnap ->
                            if (userSnap.exists()) {
                                userFound = true
                                Log.d("AuthViewModel", "Found user in company: $companyId")
                                createUserModel(userSnap, userId, companyId)
                            }
                        }
                }
                viewModelScope.launch {
                    delay(1000)
                    if(!userFound) {
                        Log.e("AuthViewModel", "User not found in any company")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to fetch companies", e)
            }
    }
    private fun createUserModel(userDoc: DocumentSnapshot, userId: String, companyId: String?) {
        try {
            val email = userDoc.getString("email") ?: ""
            val imageUrlString = userDoc.getString("imageUrl")
            val imageUrl = if (!imageUrlString.isNullOrEmpty()) {
                try { URL(imageUrlString) } catch (e: Exception) { null }
            } else null
            val bio = userDoc.getString("bio") ?: "No bio yet"
            val companyName = userDoc.getString("companyName") ?: ""

            // Determine user status
            val status = when {
                userDoc.getBoolean("isAdmin") == true -> UserStatus.ADMIN
                userDoc.getString("status") == "SUPER_USER" -> UserStatus.SUPER_USER
                else -> UserStatus.USER
            }

            val companyProfileModel = CompanyProfileModel(
                email = email,
                imageUrl = imageUrl,
                bio = bio,
                status = status,
                id = userId,
                companyName = companyName,
                companyId = companyId ?: companyName
            )

            _companyProfile.value = companyProfileModel
            Log.d("AuthViewModel", "User model created: $companyProfileModel")
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error creating UserModel", e)
        }
    }
    fun fetchCurrentUserModel() {
        val userId = getCurrentUserUid() ?: return
        Log.d("AuthViewModel", "Fetching user model for ID: $userId")

        // Try direct company path first
        firestore.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userSnapshots ->
                if (userSnapshots.exists()) {
                    Log.d("AuthViewModel", "Found user in users collection")
                }
                searchUserInCompanies(userId)
                }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to fetch user from users collection", e)
                searchUserInCompanies(userId)
            }
    }

    fun testRegToCompany(companyId: String) {
        val randomSuffix = (1000..9999).random()
        val randomEmail = "user${randomSuffix}@example.com"
        val password = "Test123!"

        // First create the user account
        Firebase.auth.createUserWithEmailAndPassword(randomEmail, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Create user profile
                    val profileModel = ProfileModel(
                        userId = userId,
                        email = randomEmail,
                        firstName = "TestUser",
                        lastName = "TestssonUser",
                        companyList = listOf(companyId)
                    )

                    // Add to Firestore first
                    firestore.collection("users").document(userId)
                        .set(profileModel)
                        .addOnSuccessListener {
                            Log.d("AuthViewModel", "User profile created, now adding to company")

                            // Only after profile is created, add to company
                            registerUserToCompany(
                                companyId = companyId,
                                onSuccess = {
                                    Log.d("AuthViewModel", "Successfully added user to company")
                                },
                                onError = { error ->
                                    Log.e("AuthViewModel", "Failed to add user to company: $error")
                                }
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e("AuthViewModel", "Failed to create user profile: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to create user: ${e.message}")
            }
    }


    fun registerAsCompany(
        email: String,
        password: String,
        confirmPassword: String,
        companyName: String,
        companyId: String,
        firstName: String,
        lastName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val errors = mutableListOf<RegisterErrors>().apply {
            addAll(validateEmail(email))
            addAll(validatePassword(password))
            addAll(validateConfirmPassword(password, confirmPassword))
        }

        if (errors.isNotEmpty()) {
            _registerErrorMessage.value = errors
            onError("Please fix the validation errors")
            return
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {

                    val companyInfo = CompanyModel(
                        companyName = companyName,
                        companyId = companyId,
                        ownerUID = userId,
                        bio = "",
                        imageUrl = null
                    )

                    val companyAdmin = CompanyProfileModel(
                        email = email,
                        imageUrl = null,
                        bio = "",
                        status = UserStatus.ADMIN,
                        id = userId,
                        companyName = companyName,
                        companyId = companyId  // Important! Set the companyId
                    )

                    val profileModel = ProfileModel(
                        userId = userId,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        companyList = listOf(companyName),
                    )

                    val companyRef = firestore.collection("companies").document(companyId)
                    val userRef = firestore.collection("users").document(userId)
                    val companyUserRef = companyRef.collection("users").document(userId)

                    // 6. Execute Firestore operations with proper error handling
                    companyRef.set(companyInfo)
                        .addOnSuccessListener {
                            Log.d("AuthViewModel", "Company created: $companyInfo")
                            userRef.set(profileModel)
                                .addOnSuccessListener {
                                    Log.d("AuthViewModel", "User profile created")
                                    companyUserRef.set(companyAdmin)
                                        .addOnSuccessListener {
                                            Log.d("AuthViewModel", "Company admin created")
                                            // 7. Update login state
                                            checkLoggedin()
                                            fetchCurrentUserModel()
                                            onSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("AuthViewModel", "Failed to create company admin", e)
                                            onError("Failed to create company admin: ${e.message}")
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("AuthViewModel", "Failed to create user profile", e)
                                    onError("Failed to create user profile: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("AuthViewModel", "Failed to create company", e)
                            onError("Failed to create company: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Failed to create user account", e)
                onError("Failed to create user account: ${e.message}")
            }
    }

    fun registerAsUser(
        email: String,
        password: String,
        confirmPassword: String,
        ) {

        val checkError = mutableListOf<RegisterErrors>().apply {
            addAll(validateEmail(email))
            addAll(validatePassword(password))
            addAll(validateConfirmPassword(password, confirmPassword))
        }
        _registerErrorMessage.value = checkError

        if(checkError.isEmpty()) {
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Basic user data
                    val profileModel = ProfileModel(
                        email = email,
                        firstName = "",
                        lastName = "",
                        userId = userId,
                        companyList = emptyList()
                    )
                    _profile.value = profileModel

                    // Create full profile document right away
                    firestore.collection("users").document(userId).set(profileModel).addOnSuccessListener {
                        // Update login state
                        checkLoggedin()
                    } .addOnFailureListener {
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
    private fun updateUserCompanyList(userId: String, companyId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                val currentCompanyList = userDoc.get("companyList") as? List<String> ?: listOf()

                // Only add if not already in list
                if (!currentCompanyList.contains(companyId)) {
                    val updatedList = currentCompanyList + companyId
                    firestore.collection("users").document(userId)
                        .update("companyList", updatedList)
                        .addOnFailureListener { e ->
                            Log.e("AuthViewModel", "Failed to update company list", e)
                        }
                }
            }
    }

    fun registerUserToCompany(companyId: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId == null) {
            onError("User not logged in")
            return
        }

        // First check if company exists
        firestore.collection("companies").document(companyId).get()
            .addOnSuccessListener { companyDoc ->
                if (!companyDoc.exists()) {
                    onError("Company does not exist")
                    return@addOnSuccessListener
                }

                val companyName = companyDoc.getString("companyName") ?: ""

                // Get current user data to complete the UserModel
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        val email = userDoc.getString("email") ?: Firebase.auth.currentUser?.email ?: ""

                        // Create complete user model
                        val user = CompanyProfileModel(
                            id = userId,
                            email = email,
                            imageUrl = null,
                            bio = "",
                            status = UserStatus.USER,
                            companyName = companyName,
                            companyId = companyId
                        )

                        // Add user to company
                        val userRef = firestore
                            .collection("companies")
                            .document(companyId)
                            .collection("users").document(userId)

                        userRef.set(user)
                            .addOnSuccessListener {
                                // Update user's company list
                                updateUserCompanyList(userId, companyId)
                                fetchCurrentUserModel() // Refresh user data
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthViewModel", "Failed to add user to company", e)
                                onError("Failed to add to company: ${e.message}")
                            }
                    }
                    .addOnFailureListener { e ->
                        onError("Failed to get user data: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onError("Failed to verify company: ${e.message}")
            }
    }

    fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checkLoggedin()
            clearLoginErrors()
            // Logga in

            fetchCurrentUserModel()


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
            // Logga in
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
            // Logga in
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
           // Logga in
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
    fun fetchProfileFirestore(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Create a complete ProfileInfo object from the document
                    val profileModel = ProfileModel(
                        email = document.getString("email") ?: "",
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: "",
                        userId = userId
                    )

                    _profile.value = profileModel
                    Log.d("ProfileFetch", "Successfully fetched profile for $userId")
                } else {
                    _profile.value = null
                    Log.d("ProfileFetch", "No profile document exists for $userId")
                }
            }
            .addOnFailureListener { e ->
                _profile.value = null
                Log.e("ProfileFetch", "Failed to fetch profile", e)
            }
    }

}


