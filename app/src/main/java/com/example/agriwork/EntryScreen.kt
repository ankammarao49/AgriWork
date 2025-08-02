package com.example.agriwork

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.example.agriwork.ui.theme.Poppins
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EntryScreen(onGetStarted: () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val bgcolor = MaterialTheme.colorScheme.primary
    var  textColor = Color.White

    SideEffect {
        systemUiController.setStatusBarColor(
            color = bgcolor,
            darkIcons = false
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgcolor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(45.dp))
                Text(
                    text = "Welcome to \nAgriWork",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Connecting Farmers with Field Workers — Empowering Rural Livelihoods",
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    color = textColor.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 17.sp
                    ),
                )
            }

//            Image(
//                painter = painterResource(id = R.drawable.farmer_cuate), // Add your own illustration!
//                contentDescription = "Welcome Illustration",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
//                contentScale = ContentScale.Crop
//            )

            PrimaryButton(
                onClick = {onGetStarted()},
                    text = "Get Started",
                textColor = bgcolor,
                buttonColor = textColor
            )
        }
    }
}

