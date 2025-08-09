package com.example.agriwork.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.ui.theme.Poppins

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
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {

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
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontFamily = Poppins
                    )
                }

                Surface(
                    color = Color(0xFFE0EEFF),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(
                        text = "$workersNeeded workers needed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = Color(0xFF3F51B5), // deep orange
                        fontFamily = Poppins
                    )
                }
            }

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

            // Detail Info Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DetailRow(label = "Location", value = location)
                DetailRow(label = "Time Required", value = "$daysRequired day${if (daysRequired > 1) "s" else ""}")
                DetailRow(label = "Land Area", value = "$acres acres") // Moved acres down here
                DetailRow(label = "Applied", value = "$noOfWorkersApplied")
                DetailRow(label = "Selected", value = "$noOfWorkersSelected")
            }

            Divider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

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
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "View Applicants",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
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
