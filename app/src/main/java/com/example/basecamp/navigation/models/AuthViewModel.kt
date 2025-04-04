package com.example.basecamp.navigation.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel : ViewModel() {

    private val tag = "AuthViewModel: "

    val database = Firebase.database.reference
    val firestore = Firebase.firestore

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    private val _userInfo = MutableStateFlow<Pair<String?, String?>>(Pair(null, null))
    val userInfo = _userInfo.asStateFlow()

    private val _registerErrorMessage = MutableStateFlow(listOf<RegisterErrors>())
    val registerErrorMessage = _registerErrorMessage.asStateFlow()

    private val _emailValid = MutableStateFlow(false)
    val emailValid = _emailValid.asStateFlow()

    private val _passwordValid = MutableStateFlow(false)
    val passwordValid = _passwordValid.asStateFlow()

    private val _confirmPasswordValid = MutableStateFlow(false)
    val confirmPasswordValid = _confirmPasswordValid.asStateFlow()

    val hasEmailError = registerErrorMessage.map { errors ->
    errors.any { it in listOf(
        RegisterErrors.EMAIL_EMPTY,
        RegisterErrors.EMAIL_NO_AT,
        RegisterErrors.EMAIL_NO_DOT)
    } }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    var errorMessages = mapOf(
        RegisterErrors.EMAIL_EMPTY to "Email cannot be empty",
        RegisterErrors.EMAIL_NO_AT to "Email must contain @",
        RegisterErrors.EMAIL_NO_DOT to "Email must contain .",
        RegisterErrors.PASSWORD_EMPTY to "Password cannot be empty",
        RegisterErrors.PASSWORD_TOO_SHORT to "Password must be at least 6 characters long",
        RegisterErrors.PASSWORD_NO_SPECIAL_CHAR to "Password must contain at least one special character",
        RegisterErrors.PASSWORD_NO_UPPERCASE to "Password must contain at least one uppercase letter",
        RegisterErrors.PASSWORD_NO_NUMBER to "Password must contain at least one number",
        RegisterErrors.CONFIRM_PASSWORD_EMPTY to "Confirm password cannot be empty",
        RegisterErrors.CONFIRM_PASSWORD_MISMATCH to "Passwords do not match"
    )


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
            Log.i("LOGINDEBUG", "Checked login")
        }.addOnFailureListener {
            println(tag + "Login failed ${it.message}")
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

    fun registerAndCreateUserInFirestore(email: String, password: String, confirmPassword: String, companyName : String?) {

        val checkError = mutableListOf<RegisterErrors>().apply {
            addAll(validateEmail(email))
            addAll(validatePassword(password))
            addAll(validateConfirmPassword(password, confirmPassword))
        }


        _registerErrorMessage.value = checkError

        if(checkError.isEmpty()) {
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
                    println(tag + "Register failed ${it.message}")
                }
        } else {
            Log.i(tag, "Errors: $checkError")
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

    fun validateEmailLive(email: String) {
        _emailValid.value = validateEmail(email).isEmpty()
    }

    fun validatePasswordLive(password: String) {
        _passwordValid.value = validatePassword(password).isEmpty()
    }

    fun validateConfirmPasswordLive(password: String, confirmPassword: String) {
        _confirmPasswordValid.value = validateConfirmPassword(password, confirmPassword).isEmpty()
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
        if(!email.contains("@")) {
            checkError.add(RegisterErrors.EMAIL_NO_AT)
        }
        if(!email.contains(".")) {
            checkError.add(RegisterErrors.EMAIL_NO_DOT)
        }
        return checkError
    }

/*
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
*/
}

enum class RegisterErrors {
    PASSWORD_EMPTY,
    PASSWORD_TOO_SHORT,
    PASSWORD_NO_SPECIAL_CHAR,
    PASSWORD_NO_UPPERCASE,
    PASSWORD_NO_NUMBER,
    CONFIRM_PASSWORD_EMPTY,
    CONFIRM_PASSWORD_MISMATCH,
    EMAIL_EMPTY,
    EMAIL_NO_AT,
    EMAIL_NO_DOT
}
