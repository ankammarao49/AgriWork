package com.example.agriwork

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val role: String = "", // either "farmer" or "worker"
    val location: String = "",
    val mobileNumber: String = ""
)

fun saveUserToFirestore(user: AppUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    if (uid == null) {
        onFailure(Exception("User not authenticated"))
        return
    }
    val userWithUid = user.copy(uid = uid)
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .document(uid) // using UID as the document ID
        .set(userWithUid)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}

fun getUserFromFirestore(
    onSuccess: (AppUser) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val uid = currentUser?.uid

    if (uid == null) {
        onFailure(Exception("User not authenticated"))
        return
    }

    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val user = document.toObject(AppUser::class.java)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure(Exception("Failed to parse user data"))
                }
            } else {
                onFailure(Exception("User document does not exist"))
            }
        }
        .addOnFailureListener { e -> onFailure(e) }
}

fun checkIfUserProfileExists(
    onExists: () -> Unit,
    onNotExists: () -> Unit,
    onError: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val uid = currentUser?.uid
    if (uid == null) {
        onNotExists() // treat as new user if UID is not available
        return
    }

    db.collection("users").document(uid)
        .get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                onExists()
            } else {
                onNotExists()
            }
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}

