package com.example.agriwork

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid
    val phone = auth.currentUser?.phoneNumber
    var userData by remember { mutableStateOf<AppUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        uid?.let {
            FirebaseFirestore.getInstance().collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { doc ->
                    userData = doc.toObject(AppUser::class.java)
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    TextButton(onClick = {
                        auth.signOut()
                        onLogout()
                    }) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                userData != null -> {
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("👤 Name: ${userData!!.name}", style = MaterialTheme.typography.titleLarge)
                            Text("📱 Phone: ${phone ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                            Text("📍 Location: ${userData!!.location}", style = MaterialTheme.typography.bodyMedium)
                            Text("🧑‍🌾 Role: ${userData!!.role.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                }
                else -> {
                    Text("Failed to load user data.")
                }
            }
        }
    }
}
