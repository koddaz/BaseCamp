package com.example.basecamp.navigation.models

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginModel : ViewModel() {

    val database = Firebase.database.reference

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()


    init {
        checklogin()
    }

    fun checklogin() {
        if (Firebase.auth.currentUser == null) {
            _loggedin.value = false
        } else {
            _loggedin.value = true
        }
    }

    fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checklogin()
        }.addOnFailureListener {
            // VISA FEL
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

    fun forgotPassword() {
        // Kod
    }

    fun register(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            checklogin()
        }.addOnFailureListener {
            // VISA FEL
        }
    }

    fun logout() {
        Firebase.auth.signOut()
        checklogin()
    }

    fun deleteUser() {
        Firebase.auth.currentUser?.delete()
        checklogin()

    }



}