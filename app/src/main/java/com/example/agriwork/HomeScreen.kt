package com.example.agriwork

import GreetingWithName
import LogoutConfirmationDialog
import UserProfileDrawer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    navController: NavHostController
) {
    val auth = FirebaseAuth.getInstance()
    var userData by remember { mutableStateOf<AppUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutConfirm by remember { mutableStateOf(false) }
    val categories = listOf(
        "Plowing", "Sowing", "Weeding", "Irrigation",
        "Harvesting", "Fertilizer", "Fence Repair", "Planting", "Digging", "Cutting"
    )

    // Fetch user data
    LaunchedEffect(Unit) {
        getUserFromFirestore(
            onSuccess = { user ->
                userData = user
                isLoading = false
            },
            onFailure = { error ->
                errorMessage = error.message
                isLoading = false
            }
        )
    }

    // Logout confirmation
    if (showLogoutConfirm) {
        LogoutConfirmationDialog(
            onConfirm = {
                auth.signOut()
                onLogout()
                showLogoutConfirm = false
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            UserProfileDrawer(
                userData = userData,
                onLogoutClick = { showLogoutConfirm = true }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            // Top App Bar as first scrollable item
            item {
                TopAppBar(
                    title = { Text("Home", modifier = Modifier.padding(start = 8.dp)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            },
                            modifier = Modifier
                                .background(Color.Black, shape = CircleShape)
                                .size(35.dp)
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Open Drawer",
                                tint = Color.White,
                                modifier = Modifier.size(25.dp))
                        }
                    }
                )
            }

            // Loading
            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // Error
            errorMessage?.let {
                item {
                    Text(
                        "Error: $it",
                        color = Color.Red,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            // User Data Loaded
            userData?.let { user ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 24.dp),
                    )
                    {
                        GreetingWithName(user.name)
                        when (user.role.lowercase()) {
                            "farmer" -> FarmerHomeContent(user, categories, navController)

                            "worker" -> WorkerHomeContent(user, categories, navController)
                        }
                    }
                }
            }

            // Fallback
            if (!isLoading && userData == null && errorMessage == null) {
                item {
                    Text("Could not load your data. Please try again.")
                }
            }
        }
    }
}

@Composable
fun FarmerHomeContent(currentUser: AppUser, categories: List<String>, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        PrimaryButton(onClick = { navController.navigate("creatework") }, text = "Post a Work")

        HorizontalDivider()

        WorkCategorySection(
            title = "Work Categories",
            categories = categories,
            onCategoryClick = { category ->
                navController.navigate("creatework/$category")
            }
        )

        HorizontalDivider()

        FarmerDashboardScreen(currentUser, navController)
    }
}

@Composable
fun WorkerHomeContent(currentUser: AppUser, categories: List<String>, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {

//        PrimaryButton(onClick = {  }, text = "Find a Work")
//
//        HorizontalDivider()
//
//        WorkCategorySection(
//            title = "Work Categories",
//            categories = categories,
//            onCategoryClick = { category ->
//            }
//        )

        HorizontalDivider()

        WorkerDashboardScreen(currentUser, navController)
    }
}
