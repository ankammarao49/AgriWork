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
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.example.agriwork.ui.theme.Poppins

@Composable
fun AgriWorkHome() {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(24.dp)

    ) {
        Box(modifier = Modifier.padding(top = 90.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Welcome to \nAgriWork", // use \n if you want a manual line break
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    lineHeight = 30.sp, // increase or decrease this for more or less space between lines
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Connecting Farmers with Field Workers — Empowering Rural Livelihoods",
                    fontSize = 10.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 14.sp
                    ),
                )
            }

            Button(
                onClick = { /* TODO: Handle click */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAgriWorkHome() {
    AgriWorkTheme {
        AgriWorkHome()
    }
}

