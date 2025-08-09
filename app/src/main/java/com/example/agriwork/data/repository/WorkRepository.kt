package com.example.agriwork.data.repository

import android.util.Log
import com.example.agriwork.data.model.Work
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object WorkRepository {
    fun fetchAllWorks(onSuccess: (List<Work>) -> Unit, onFailure: (Exception) -> Unit) {
        FirebaseFirestore.getInstance().collection("works")
            .get()
            .addOnSuccessListener { snapshot ->
                val works = snapshot.documents.mapNotNull { doc ->
                    val work = doc.toObject(Work::class.java)
                    work?.copy(id = doc.id)
                }
                onSuccess(works)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun listenToWorks(onWorksUpdate: (List<Work>) -> Unit) {
        Firebase.firestore.collection("works")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e("Firestore", "Listen failed", error)
                    return@addSnapshotListener
                }

                val works = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Work::class.java)?.copy(id = doc.id)
                }

                onWorksUpdate(works)
            }
    }

    fun applyToWork(workId: String, onSuccess: () -> Unit) {
        val db = Firebase.firestore
        val workRef = db.collection("works").document(workId)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        workRef.get().addOnSuccessListener { doc ->
            val applied = doc.get("workersApplied") as? List<*> ?: emptyList<Any>()
            if (!applied.contains(uid)) {
                workRef.update("workersApplied", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener {
                        onSuccess()
                        Log.d("applyToWork", "Successfully applied")
                    }
                    .addOnFailureListener {
                        Log.e("applyToWork", "Failed: ${it.message}")
                    }
            }
        }.addOnFailureListener {
            Log.e("applyToWork", "Error fetching work: ${it.message}")
        }
    }


    fun saveWorkToFirestore(
        work: Work,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("works") // You can name the collection anything you want
            .add(work)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
