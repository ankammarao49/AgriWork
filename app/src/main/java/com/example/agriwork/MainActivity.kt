package com.example.agriwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AgriWorkAppContent(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White,
            darkIcons = true // status bar icons/text will be black
        )
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") { AgriWorkHome(navController) }
        composable("login") { LoginScreen(navController) }
        composable("farmerworkcategories") { FarmerCategoriesScreen() }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            AgriWorkTheme {
                val navController: NavHostController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AgriWorkAppContent(navController)
                }
            }
        }
    }
}