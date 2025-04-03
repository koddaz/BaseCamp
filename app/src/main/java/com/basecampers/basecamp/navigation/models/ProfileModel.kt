package com.basecampers.basecamp.navigation.models


import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileModel : ViewModel() {

    val database = Firebase.database.reference
    val firestore = Firebase.firestore

    private val _loggedin = MutableStateFlow(false)
    val loggedin = _loggedin.asStateFlow()

    init {
        checklogin()
    }

    fun checklogin() {
        _loggedin.value = Firebase.auth.currentUser != null
    }

    fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            checklogin()
        }.addOnFailureListener {

        }
    }

    fun register(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            checklogin()
        }.addOnFailureListener {

        }
    }

    fun registerAndCreateUserInFirestore(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authResult ->
            val userId = authResult.user?.uid
            if (userId != null) {
                val user = mapOf(
                    "email" to email,
                    "username" to email.substringBefore("@") // Basic username logic
                )

                firestore.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        checklogin()
                    }
                    .addOnFailureListener {
                        // Firestore
                    }
            }
        }.addOnFailureListener {
            // Auth
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

    fun getUserInfo(): Pair<String?, String?> {
        val user = Firebase.auth.currentUser
        val username = user?.displayName ?: user?.email?.substringBefore("@") // Def username
        val email = user?.email
        return Pair(username, email)
    }
}

