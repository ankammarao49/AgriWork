package com.example.agriwork

import com.google.firebase.firestore.FirebaseFirestore

data class AppUser(
    val name: String = "",
    val role: String = "", // either "farmer" or "worker"
    val location: String = "",
    val mobileNumber: String = ""
)

fun saveUserToFirestore(user: AppUser, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users") // collection name
        .document(user.mobileNumber) // use mobile number as unique ID
        .set(user)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
}
