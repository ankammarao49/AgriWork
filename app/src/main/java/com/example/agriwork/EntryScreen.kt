package com.example.agriwork

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.Poppins
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun EntryScreen(onGetStarted: () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val bgcolor = MaterialTheme.colorScheme.primary
    val textColor = Color.White

    val context = LocalContext.current

    // Set status bar color
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
                    text = stringResource(id = R.string.welcome_title),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Black,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = R.string.welcome_subtitle),
                    fontSize = 13.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    color = textColor.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 17.sp
                    ),
                )
            }

            PrimaryButton(
                onClick = { onGetStarted() },
                text = stringResource(id = R.string.get_started),
                textColor = bgcolor,
                buttonColor = textColor
            )
        }
    }

}
