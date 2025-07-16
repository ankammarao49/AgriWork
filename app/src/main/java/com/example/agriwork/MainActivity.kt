package com.example.agriwork

import AuthScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AgriWorkAppContent(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White,
            darkIcons = true
        )
    }

    NavHost(navController = navController, startDestination = "entrydecider") {
        composable("entrydecider") {
            LaunchedEffect(Unit) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    navController.navigate("createprofile") {
                        popUpTo("entrydecider") { inclusive = true }
                    }
                } else {
                    navController.navigate("entry") {
                        popUpTo("entrydecider") { inclusive = true }
                    }
                }
            }
        }

        composable("entry") {
            EntryScreen(
                onGetStarted = {
                    navController.navigate("auth") {
                        popUpTo("entry") { inclusive = true }
                    }
                }
            )
        }

        composable("auth") {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate("creteprofile") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("createprofile") {
            CreateProfileScreen { name, location, role ->
                // TODO: Save the user data to your database here (e.g., Firestore or Realtime DB)

                // Then navigate to main screen (e.g., farmer categories)
                navController.navigate("farmerworkcategories") {
                    popUpTo("createprofile") { inclusive = true }
                }
            }
        }

        composable("farmerworkcategories") {
            FarmerCategoriesScreen(navController)
        }

        composable("worksavailable") { WorksAvailableScreen() }
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)

        setContent {
            AgriWorkTheme {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    AgriWorkAppContent(navController)
                }
            }
        }
    }
}
