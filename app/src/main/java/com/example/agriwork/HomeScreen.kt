package com.example.agriwork

import GreetingWithName
import LogoutConfirmationDialog
import UserProfileDrawer
import android.os.Build
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
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    var userData by remember { mutableStateOf<AppUser?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutConfirm by remember { mutableStateOf(false) }

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
        ) {
            // Top App Bar as first scrollable item
            item {
                TopAppBar(
                    title = { Text("Home") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Person, contentDescription = "Open Drawer")
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
                    HomeScreenContent(user = user)
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
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    user: AppUser,
) {
    val currentHour = remember { LocalTime.now().hour }

    val greeting = when (currentHour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else -> "Good Night"
    }

    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
    )
    {
        Text(
            text = "$greeting\n${user.name}",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            fontSize = 30.sp,
            lineHeight = 36.sp
        )


        when (user.role.lowercase()) {
            "farmer" -> FarmerHomeContent()

            "worker" -> WorkerHomeContent()
        }
    }
}

@Composable
fun FarmerHomeContent() {
    val workCategories = listOf(
        "Plowing & Land Preparation",
        "Sowing / Seed Planting",
        "Weeding & Pest Control",
        "Irrigation & Watering",
        "Harvesting",
        "Crop Sorting & Packaging",
        "Cattle Feeding & Milking",
        "Poultry Care",
        "Cleaning Sheds / Barns",
        "Vaccination / Health Check Support",
        "Grazing Assistance",
        "Tractor Driving / Operation",
        "Farm Equipment Repair",
        "Irrigation System Maintenance",
        "Fertilizer / Pesticide Spraying",
        "Transporting Produce to Market",
        "Loading / Unloading Farm Goods",
        "Storage & Inventory Help",
        "Assisting in Local Delivery",
        "Farmhouse Cleaning",
        "Fence / Boundary Repair",
        "Construction (temporary sheds, etc.)",
        "Tree Planting",
        "Farm Security Watch"
    )


    var showAll by remember { mutableStateOf(false) }

    val categoriesToShow = if (showAll) workCategories else workCategories.take(4)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PrimaryButton(
            onClick = { /* TODO: Navigate to Find Workers screen */ },
            text = "Post a Work"
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))


        Text("Work Categories", fontFamily = Poppins)

        Spacer(modifier = Modifier.height(8.dp))

        categoriesToShow.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        // TODO: Handle category click
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF34699A) // light green background, change as needed
                ),
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color(0xFF113F67), // light green circle bg, change as you like
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Category Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = category,
                        fontFamily = Poppins,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
        // 👇 Show "See More" or "Show Less" button
        OutlinedButton(
            onClick = { showAll = !showAll },
            modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF113F67)
            ),
            border = BorderStroke(1.dp, Color(0xFF113F67))
        ) {
            Text(text = if (showAll) "Show Less" else "See More")
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Available Work",
                fontFamily = Poppins,
                modifier = Modifier.padding(bottom = 16.dp)
            )


        }
    }
}

@Composable
fun WorkerHomeContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { /* TODO: Navigate to Jobs screen */ }) {
            Text("Browse Available Jobs")
        }
        // Add more worker-specific UI here later
    }
}
