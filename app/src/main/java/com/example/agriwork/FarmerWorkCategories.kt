package com.example.agriwork

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FarmerCategoriesScreen(navController: NavController) {
    val workCategories = listOf(
        "Harvesting", "Planting Seeds", "Planting Plants", "Weeding", "Watering",
        "Plowing", "Fertilizing", "Digging"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Works You’re Hiring For",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )

        TextButton(onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Logout", color = Color.Red)
        }

    Spacer(modifier = Modifier.height(24.dp))


    LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                items(workCategories) { category ->
                    Button(
                        onClick = { /* TODO: Handle category selection */ },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA5D6A7), // light green
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        Text(category)
                    }
                }
            }
        )
    }
}