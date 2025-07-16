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

@Composable
fun WorksAvailableScreen() {
    val workItems = listOf(
        Work("Ramu", "Harvest Tomatoes", 3, 1.5, 4, 2, "Farm 12, Village A"),
        Work("Sita", "Plow Field", 2, 2.0, 3, 1, "Plot B, Sector 3"),
        Work("John", "Irrigate Crops", 4, 3.5, 5, 3, "Greenland Farms"),
        Work("Lalitha", "Collect Straw", 1, 1.0, 2, 1, "Pasture Hill"),
        Work("Krishna", "Seed Rice", 3, 2.8, 4, 2, "Valley East"),
        Work("Mohan", "Spray Fertilizer", 2, 1.2, 3, 1, "Zone 5, Sector D")
    )


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

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(workItems) { work ->
                WorkShowCard(
                    farmerName = work.farmerName,
                    workTitle = work.workTitle,
                    daysRequired = work.daysRequired,
                    acres = work.acres,
                    workersNeeded = work.workersNeeded,
                    workersSelected = work.workersSelected,
                    location = work.location
                )
            }
        }
    }
}

// Data class to model work details
data class Work(
    val farmerName: String,
    val workTitle: String,
    val daysRequired: Int,
    val acres: Double,
    val workersNeeded: Int,
    val workersSelected: Int,
    val location: String
)

@Composable
fun WorkShowCard(
    modifier: Modifier = Modifier,
    farmerName: String,
    workTitle: String,
    daysRequired: Int,
    acres: Double,
    workersNeeded: Int,
    workersSelected: Int,
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
                        text = "$workersSelected / $workersNeeded workers",
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
