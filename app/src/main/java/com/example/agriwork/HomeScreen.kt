package com.example.agriwork

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth

fun logout(navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate("auth") {
        popUpTo(0) // Clears backstack so back press won’t return to home
    }
}

@Composable
fun AgriWorkHome(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(45.dp)) // Adjust spacing if needed
                Text(
                    text = "Welcome to \nAgriWork",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Connecting Farmers with Field Workers — Empowering Rural Livelihoods",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 16.sp
                    )
                )
            }

            Button(onClick = {
                logout(navController)
            }) {
                Text("Logout")
            }

            Button(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ⚠️ Preview won't compile unless you provide a fake navController
@Preview(showBackground = true)
@Composable
fun PreviewAgriWorkHome() {
    AgriWorkTheme {
        val navController = rememberNavController()
        AgriWorkHome(navController)
    }
}
