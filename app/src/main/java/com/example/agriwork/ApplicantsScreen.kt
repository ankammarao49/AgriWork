package com.example.agriwork

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

@Composable
fun ApplicantsScreen(workId: String, navController: NavHostController) {
    val db = Firebase.firestore
    var farmer by remember { mutableStateOf<AppUser?>(null) }
    var applicants by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var selectedApplicants by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf("Applied") }

    LaunchedEffect(workId) {
        db.collection("works").document(workId).get()
            .addOnSuccessListener { document ->
                val farmerUid = document["postedBy"] as? String
                val appliedUids = document["workersApplied"] as? List<String> ?: emptyList()
                val selectedUids = document["workersSelected"] as? List<String> ?: emptyList()
                selectedApplicants = selectedUids

                // Fetch applicants
                if (appliedUids.isNotEmpty()) {
                    db.collection("users")
                        .whereIn("uid", appliedUids)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            applicants = snapshot.documents.mapNotNull { it.toObject(AppUser::class.java) }
                            isLoading = false
                        }
                } else {
                    isLoading = false
                }

                // Fetch farmer
                farmerUid?.let {
                    db.collection("users").document(it).get()
                        .addOnSuccessListener { userDoc ->
                            farmer = userDoc.toObject(AppUser::class.java)
                        }
                }
            }
    }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with back button and count
        farmer?.let {
            FarmerDetailsCard(it)
            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF113F67))
            }
            Text(
                text = "Applicants",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Poppins,
            )
        }

        Text(
            text = "Applied: ${applicants.size} | Selected: ${selectedApplicants.size}",
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.DarkGray,
            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        Row {
            listOf("Applied", "Selected").forEach { tab ->
                val isActive = tab == selectedTab
                TextButton(
                    onClick = { selectedTab = tab },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        tab,
                        fontFamily = Poppins,
                        color = if (isActive) Color.Black else Color.Gray,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
            val filteredList = when (selectedTab) {
                "Applied" -> applicants.filter { !selectedApplicants.contains(it.uid) }
                "Selected" -> applicants.filter { selectedApplicants.contains(it.uid) }
                else -> emptyList()
            }

            if (filteredList.isEmpty()) {
                Text("No ${selectedTab.lowercase()} applicants.", fontFamily = Poppins)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredList) { user ->
                        ApplicantCard(
                            user = user,
                            isAlreadySelected = selectedApplicants.contains(user.uid),
                            onApproveClick = {
                                approveApplicant(
                                    workId = workId,
                                    userId = user.uid,
                                    onSuccess = {
                                        selectedApplicants = selectedApplicants + user.uid
                                    },
                                    onError = { /* Handle error */ }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FarmerDetailsCard(farmer: AppUser) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Farmer Info", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Farmer Name", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(farmer.name, fontFamily = Poppins, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(farmer.location, fontFamily = Poppins, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(farmer.mobileNumber, fontFamily = Poppins, color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun ApplicantCard(user: AppUser, isAlreadySelected: Boolean, onApproveClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Name", tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Poppins,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    user.location,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = "Mobile", tint = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    user.mobileNumber,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
            }

            if (!isAlreadySelected) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onApproveClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Approve", fontFamily = Poppins, color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color(0xFF388E3C))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Selected",
                        fontFamily = Poppins,
                        color = Color(0xFF388E3C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun approveApplicant(workId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val db = Firebase.firestore
    val workRef = db.collection("works").document(workId)

    workRef.update("workersSelected", FieldValue.arrayUnion(userId))
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it.message ?: "Unknown error") }
}
