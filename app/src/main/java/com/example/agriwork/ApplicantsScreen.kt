package com.example.agriwork

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.Divider
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
    var work by remember { mutableStateOf<Work?>(null) }
    var applicants by remember { mutableStateOf<List<AppUser>>(emptyList()) }
    var selectedApplicants by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf("Applied") }

    LaunchedEffect(workId) {
        db.collection("works").document(workId).get()
            .addOnSuccessListener { document ->
                val workObj = document.toObject(Work::class.java)
                work = workObj
                selectedApplicants = workObj?.workersSelected ?: emptyList()

                val appliedUids = workObj?.workersApplied ?: emptyList()
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
            }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // 🔙 Back + Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF113F67))
                }
                Text(
                    text = "Applicants",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Poppins
                )
            }

            // 👥 Count Text
            Text(
                text = "Applied: ${applicants.size} | Selected: ${selectedApplicants.size} | Needed: ${work?.workersNeeded ?: "-"}",
                fontSize = 14.sp,
                fontFamily = Poppins,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 📄 Work and Farmer Info
        item {
            WorkAndFarmerCard(work ?: return@item)
        }

        // 🧾 Sleek Tab Buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Applied", "Selected").forEach { tab ->
                    val isActive = tab == selectedTab
                    OutlinedButton(
                        onClick = { selectedTab = tab },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isActive) Color.Black else Color.White,
                            contentColor = if (isActive) Color.White else Color.Black
                        ),
                        border = BorderStroke(1.dp, if (isActive) Color.Black else Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = tab,
                            fontFamily = Poppins,
                            fontSize = 14.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }


        // 🌀 Loading Indicator
        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            }
        } else {
            // 👇 Filtered list based on tab
            val filteredList = when (selectedTab) {
                "Applied" -> applicants.filter { !selectedApplicants.contains(it.uid) }
                "Selected" -> applicants.filter { selectedApplicants.contains(it.uid) }
                else -> emptyList()
            }

            if (filteredList.isEmpty()) {
                item {
                    Text("No ${selectedTab.lowercase()} applicants.", fontFamily = Poppins)
                }
            } else {
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

@Composable
fun WorkAndFarmerCard(work: Work) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Text(
                text = "Work & Farmer Details",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid layout: 2 columns for clarity
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TwoColumnRow("Work Title", work.workTitle)
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                TwoColumnRow("Days Required", "${work.daysRequired}")
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                TwoColumnRow("Acres", "${work.acres}")
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                TwoColumnRow("Farmer Name", work.farmer.name)
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                TwoColumnRow("Location", work.farmer.location)
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                TwoColumnRow("Phone", work.farmer.mobileNumber)
            }
        }
    }
}

@Composable
fun TwoColumnRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ApplicantCard(user: AppUser, isAlreadySelected: Boolean, onApproveClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoRow(label = "Name", value = user.name)
            InfoRow(label = "Location", value = user.location)
            InfoRow(label = "Mobile", value = user.mobileNumber)

            if (!isAlreadySelected) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onApproveClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Approve", fontFamily = Poppins, color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Selected",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}


fun approveApplicant(workId: String, userId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val db = Firebase.firestore
    val workRef = db.collection("works").document(workId)

    workRef.update("workersSelected", FieldValue.arrayUnion(userId))
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError(it.message ?: "Unknown error") }
}
