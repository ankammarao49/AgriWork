package com.example.agriwork

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.util.UUID

// Data class to model work details
data class Work(
    val id: String = UUID.randomUUID().toString(),
    val farmer: AppUser = AppUser(),
    val workTitle: String = "",
    val daysRequired: Int = 0,
    val acres: Double = 0.0,
    val workersNeeded: Int = 0,
    val workersSelected: List<String>? = null,
    val workersApplied: List<String>? = null,
)

@Composable
fun WorkCategorySection(
    title: String,
    categories: List<String>,
    onCategoryClick: (String) -> Unit
) {
    var showAll by remember { mutableStateOf(false) }

    val categoriesToShow = if (showAll) categories else categories.take(4)

    Column {
        Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        categoriesToShow.forEach { category ->
            CategoryCard(category = category) {
                onCategoryClick(category)
            }
        }

        OutlinedButton(
            onClick = { showAll = !showAll },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF113F67)),
            border = BorderStroke(1.dp, Color(0xFF113F67))
        ) {
            Text(if (showAll) "Show Less" else "See More")
        }
    }
}

@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF34699A))
    )
    {
        Column(modifier = Modifier.fillMaxWidth().padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(48.dp).background(color = Color(0xFF113F67), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBox, contentDescription = "Category Icon", tint = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = category, fontFamily = Poppins, textAlign = TextAlign.Center, color = Color.White)
        }
    }
}


@Composable
fun AvailableWorkSection(currentUser: AppUser)
{
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val workList = remember { mutableStateListOf<Work>() }

    LaunchedEffect(Unit) {
        fetchAllWorksFromFirestore(
            onSuccess = {
                workList.clear()
                workList.addAll(it)
                isLoading = false
            },
            onFailure = { error = it.message; isLoading = false }
        )
    }

    Column {
        Text("Available Work", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(25.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("Error: $error", color = Color.Red)
            workList.isEmpty() -> Text("No work available at the moment.")
            else -> workList.forEach { work ->
                val hasAlreadyApplied = work.workersApplied?.contains(currentUid) == true
                WorkShowCard(
                    farmerName = work.farmer.name,
                    workTitle = work.workTitle,
                    daysRequired = work.daysRequired,
                    acres = work.acres,
                    workersNeeded = work.workersNeeded,
                    noOfWorkersSelected = work.workersSelected?.size ?: 0,
                    noOfWorkersApplied = work.workersApplied?.size ?: 0,
                    location = work.farmer.location,
                    onApplyClick = {
                        applyToWork(work.id) {
                            // update local workList
                            val index = workList.indexOfFirst { it.id == work.id }
                            if (index != -1) {
                                val updatedWork = work.copy(
                                    workersApplied = (work.workersApplied ?: emptyList()) + currentUid
                                )
                                workList[index] = updatedWork
                            }
                        }
                    },
                    applyButtonEnabled = !hasAlreadyApplied
                )
            }
        }
    }
}

@Composable
fun WorkShowCard(
    modifier: Modifier = Modifier,
    farmerName: String,
    workTitle: String,
    daysRequired: Int,
    acres: Double,
    workersNeeded: Int,
    noOfWorkersApplied: Int,
    noOfWorkersSelected: Int,
    location: String,
    onApplyClick: () -> Unit = {},
    applyButtonEnabled: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFB2DFDB))
                    .padding(vertical = 15.dp, horizontal = 20.dp)
            ) {
                Text(
                    text = workTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))


            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = farmerName,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$acres acres",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
                Text(
                    text = location,
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
                Text(
                    text = "Estimated Time: $daysRequired day${if (daysRequired > 1) "s" else ""}",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )
                Text(
                    text = "Workers Needed: $workersNeeded",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )

                Text(
                    text = "Workers Applied: $noOfWorkersApplied",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color(0xFF666666) // Slightly lighter to show it's pending
                )

                Text(
                    text = "Workers Selected: $noOfWorkersSelected",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    color = Color.DarkGray
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apply Button
        Button(
            onClick = onApplyClick,
            enabled = applyButtonEnabled, // 👈 Disable if already applied
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(40.dp)
        ) {
            Text(
                text = if (applyButtonEnabled) "Apply" else "Already Applied",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

fun fetchAllWorksFromFirestore(
    onSuccess: (List<Work>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    FirebaseFirestore.getInstance().collection("works")
        .get()
        .addOnSuccessListener { snapshot ->
            val works = snapshot.documents.mapNotNull { doc ->
                val work = doc.toObject(Work::class.java)
                work?.copy(id = doc.id) // use Firestore doc ID instead of the random UUID
            }
            onSuccess(works)
        }
        .addOnFailureListener { onFailure(it) }
}


fun applyToWork(workId: String, onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val workRef = db.collection("works").document(workId)

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    if (uid == null) {
        Log.e("applyToWork", "User not logged in. UID is null.")
        return
    }

    workRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val workersApplied = document.get("workersApplied") as? List<*> ?: emptyList<Any>()

            if (workersApplied.contains(uid)) {
                Log.d("applyToWork", "User already applied. No update needed.")
            } else {
                workRef.update(
                    mapOf(
                        "workersApplied" to FieldValue.arrayUnion(uid)
                    )
                ).addOnSuccessListener {
                    onSuccess()
                    Log.d("applyToWork", "Successfully applied to work.")
                }.addOnFailureListener {
                    Log.e("applyToWork", "Failed to apply: ${it.message}")
                }
            }
        } else {
            Log.e("applyToWork", "Work document does not exist.")
        }
    }.addOnFailureListener { exception ->
        Log.e("applyToWork", "Error fetching work: ${exception.message}")
    }
}
