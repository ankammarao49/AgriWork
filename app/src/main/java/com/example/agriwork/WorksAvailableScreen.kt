package com.example.agriwork

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
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
fun WorkListSection(
    title: String,
    workList: List<Work>,
    currentUid: String,
    currentUserRole: String,
    filterType: WorkFilterType,
    onApplyClick: (Work) -> Unit,
    navController: NavHostController
) {
    val filteredWorks = filterWorks(workList, currentUid, filterType)

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(15.dp))

        if (filteredWorks.isEmpty()) {
            Text("No work found in this section.")
        } else {
            filteredWorks.forEach { work ->
                val hasAlreadyApplied = work.workersApplied?.contains(currentUid) == true
                val isWorkFull = (work.workersSelected?.size ?: 0) >= work.workersNeeded
                val canApply = !hasAlreadyApplied && !isWorkFull

                WorkShowCard(
                    farmerName = work.farmer.name,
                    workTitle = work.workTitle,
                    daysRequired = work.daysRequired,
                    acres = work.acres,
                    workersNeeded = work.workersNeeded,
                    noOfWorkersSelected = work.workersSelected?.size ?: 0,
                    noOfWorkersApplied = work.workersApplied?.size ?: 0,
                    location = work.farmer.location,
                    showApplyButton = shouldShowApplyButton(
                        currentUserRole = currentUserRole,
                        currentUid = currentUid,
                        work = work,
                    ),
                    onApplyClick = { onApplyClick(work) },
                    onViewApplicantsClick = {
                        navController.navigate("applicants/${work.id}")
                    }

                )
            }
        }
    }
}

@Composable
fun FarmerDashboardScreen(currentUser: AppUser, navController: NavHostController) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val workList = remember { mutableStateListOf<Work>() }

    LaunchedEffect(Unit) {
        listenToWorks { updatedWorks ->
            workList.clear()
            workList.addAll(updatedWorks)
            isLoading = false
        }
    }

    Column {
        Text("Your Posted Works", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("Error: $error", color = Color.Red)
            else -> {
                val createdByMe = workList.filter { it.farmer.uid == currentUid }

                WorkListSection(
                    title = "Works You Posted",
                    workList = createdByMe,
                    currentUid = currentUid,
                    currentUserRole = "farmer",
                    filterType = WorkFilterType.CREATED_BY_ME,
                    navController = navController,
                    onApplyClick = { /* Farmers don’t apply, so no apply callback */ },
                )
            }
        }
    }
}

@Composable
fun WorkerDashboardScreen(currentUser: AppUser, navController: NavHostController) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val workList = remember { mutableStateListOf<Work>() }

    LaunchedEffect(Unit) {
        listenToWorks { updatedWorks ->
            workList.clear()
            workList.addAll(updatedWorks)
            isLoading = false
        }
    }

    Column {
        Text("Available Work", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("Error: $error", color = Color.Red)
            else -> {
                val filterConfigs = listOf(
                    "Available Work" to WorkFilterType.AVAILABLE,
                    "Work You Applied" to WorkFilterType.APPLIED,
                    "Work You're Selected For" to WorkFilterType.SELECTED
                )

                filterConfigs.forEach { (title, filterType) ->
                    WorkListSection(
                        title = title,
                        workList = workList,
                        currentUid = currentUid,
                        currentUserRole = "worker",
                        filterType = filterType,
                        onApplyClick = { work ->
                            applyToWork(work.id) {
                                val index = workList.indexOfFirst { it.id == work.id }
                                if (index != -1) {
                                    val updated = work.copy(
                                        workersApplied = (work.workersApplied ?: emptyList()) + currentUid
                                    )
                                    workList[index] = updated
                                }
                            }
                        },
                        navController = navController
                    )
                }
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
    onViewApplicantsClick: () -> Unit = {},
    showApplyButton: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(bottom = 20.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header with prominent Workers Needed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = workTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                    Text(
                        text = "by $farmerName",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "$workersNeeded workers needed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color(0xFFD84315), // deep orange
                        fontFamily = Poppins
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detail Info Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DetailRow(label = "Location", value = location)
                DetailRow(label = "Time Required", value = "$daysRequired day${if (daysRequired > 1) "s" else ""}")
                DetailRow(label = "Land Area", value = "$acres acres") // Moved acres down here
                DetailRow(label = "Applied", value = "$noOfWorkersApplied")
                DetailRow(label = "Selected", value = "$noOfWorkersSelected")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            if (showApplyButton) {
                Button(
                    onClick = onApplyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Apply",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins
                    )
                }
            }
            else {
                OutlinedButton(
                    onClick = onViewApplicantsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF37474F))
                ) {
                    Text(
                        text = "View Applicants",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                        color = Color(0xFF37474F)
                    )
                }
            }
        }
    }
}


@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontFamily = Poppins,
            color = Color.DarkGray
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium
        )
    }
}

// WorkFilters.kt
fun filterWorks(
    workList: List<Work>,
    currentUid: String,
    type: WorkFilterType
): List<Work> {
    return when (type) {
        WorkFilterType.ALL -> workList
        WorkFilterType.AVAILABLE -> workList.filter { work ->
            val hasApplied = work.workersApplied?.contains(currentUid) == true
            val isFull = (work.workersSelected?.size ?: 0) >= work.workersNeeded
            !hasApplied && !isFull
        }
        WorkFilterType.APPLIED -> workList.filter { it.workersApplied?.contains(currentUid) == true }
        WorkFilterType.CREATED_BY_ME -> workList.filter { it.farmer.uid == currentUid }
        WorkFilterType.SELECTED -> workList.filter { it.workersSelected?.contains(currentUid) == true }
    }
}

enum class WorkFilterType {
    ALL,
    AVAILABLE,
    APPLIED,
    CREATED_BY_ME,
    SELECTED
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

fun listenToWorks(onWorksUpdate: (List<Work>) -> Unit) {
    val db = Firebase.firestore

    db.collection("works")
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

fun shouldShowApplyButton(
    currentUserRole: String,
    currentUid: String,
    work: Work
): Boolean {
    if (currentUserRole == "farmer") return false            // Farmers never apply
    if (work.farmer.uid == currentUid) return false          // You posted it
    if (work.workersApplied?.contains(currentUid) == true) return false
    if ((work.workersSelected?.size ?: 0) >= work.workersNeeded) return false
    return true
}
