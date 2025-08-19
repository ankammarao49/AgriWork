package com.example.agriwork

import AuthScreen
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agriwork.data.repository.DataStorePreferenceRepository
import com.example.agriwork.ui.language.DataStoreViewModelFactory
import com.example.agriwork.ui.language.LanguageViewModel
import com.example.agriwork.ui.screens.CreateWorkScreen
import com.example.agriwork.ui.theme.AgriWorkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.example.agriwork.ui.language.setLanguage
import kotlinx.coroutines.launch

@Composable
fun AgriWorkAppContent(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(color = Color.White, darkIcons = true)
    }

    NavHost(navController = navController, startDestination = "entrydecider") {
        composable("entrydecider") {
            LaunchedEffect(Unit) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    checkIfUserProfileExists(
                        onExists = {
                            navController.navigate("home") {
                                popUpTo("entrydecider") { inclusive = true }
                            }
                        },
                        onNotExists = {
                            navController.navigate("createprofile") {
                                popUpTo("entrydecider") { inclusive = true }
                            }
                        },
                        onError = {
                            // Fallback: Navigate to create profile in case of error
                            navController.navigate("createprofile") {
                                popUpTo("entrydecider") { inclusive = true }
                            }
                        }
                    )
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
                    checkIfUserProfileExists(
                        onExists = {
                            navController.navigate("home") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        onNotExists = {
                            navController.navigate("createprofile") {
                                popUpTo("auth") { inclusive = true }
                            }
                        },
                        onError = {
                            navController.navigate("createprofile") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }
            )
        }


        composable("createprofile") {
            CreateProfileScreen { name, location, role ->
                // TODO: Save user info to Firestore here
                navController.navigate("home") {
                    popUpTo("createprofile") { inclusive = true }
                }
            }
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                onLogout = {
                    navController.navigate("entry") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("farmerworkcategories") {
            FarmerCategoriesScreen(navController)
        }


        composable("creatework") {
            CreateWorkScreen(navController)
        }

        composable("creatework/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CreateWorkScreen(navController = navController, category = category)
        }

        composable("applicants/{workId}") { backStackEntry ->
            val workId = backStackEntry.arguments?.getString("workId") ?: ""
            ApplicantsScreen(workId = workId, navController = navController)
        }

    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = DataStorePreferenceRepository.getInstance(this)
        val languageViewModel = ViewModelProvider(
            this,
            DataStoreViewModelFactory(repo)
        )[LanguageViewModel::class.java]

        languageViewModel.language.observe(this) { lang ->
            // If no language saved yet, force English (0)
            if (lang == null) {
                lifecycleScope.launch {
                    repo.setLanguage(0) // 0 = English
                }
                setLanguage(this, 0)
            } else {
                setLanguage(this, lang)
            }
            // Load UI *after* language is applied
            setContent {
                AgriWorkTheme {
                    val navController = rememberNavController()
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AgriWorkAppContent(navController)
                    }
                }
            }
        }

        askNotificationPermission()
        askMicPermission()

        createNotificationChannel()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
//                sendNotification(
//                    context = context,
//                    title = "Test Notification 🚜",
//                    message = "This is your AgriWork test alert!",
//                    userId = "test_user"
//                ) // or just mark as permission granted
            } else {
                // Permission denied, maybe show a message
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "my_channel_id",
            "My Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)

        manager?.createNotificationChannel(channel)
    }

    private val requestRecordAudioPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                // show message: "Mic permission required"
            }
        }

    private fun askMicPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestRecordAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }


}


@Preview(showBackground = true)
@Composable
fun NavGraphPreview() {
    val navController = rememberNavController()
    AgriWorkAppContent(navController)
}
