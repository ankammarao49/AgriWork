package com.example.agriwork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun WorksAvailableScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Available Work",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 16.dp)
        )

    }
}

// Data class to model work details
data class Work(
    val farmerName: String = "",
    val workTitle: String = "",
    val daysRequired: Int = 0,
    val acres: Double = 0.0,
    val workersNeeded: Int = 0,
    val workersSelected: List<AppUser>? = null,
    val workersApplied: List<AppUser>? = null,
    val location: String = "",
) {
    val noOfWorkersSelected: Int
        get() = workersSelected?.size ?: 0
}

@Composable
fun WorkShowCard(
    modifier: Modifier = Modifier,
    farmerName: String,
    workTitle: String,
    daysRequired: Int,
    acres: Double,
    workersNeeded: Int,
    noOfWorkersSelected: Int,
    location: String,
    onApplyClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column {

            // Work Category Box at the top
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

            // Info Row: Two columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column
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
                }

                // Right Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Estimated Time: $daysRequired day${if (daysRequired > 1) "s" else ""}",
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "$noOfWorkersSelected / $workersNeeded workers",
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
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "Apply",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
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
            val works = snapshot.documents.mapNotNull { it.toObject(Work::class.java) }
            onSuccess(works)
        }
        .addOnFailureListener { onFailure(it) }
}