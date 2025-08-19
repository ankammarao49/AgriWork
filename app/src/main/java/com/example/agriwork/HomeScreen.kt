package com.example.agriwork

import FarmerDashboardScreen
import GreetingWithName
import LogoutConfirmationDialog
import UserProfileDrawer
import WorkCategorySection
import WorkerDashboardScreen
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import com.example.agriwork.data.model.AppUser
import com.example.agriwork.ui.components.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.unit.sp
import com.example.agriwork.ui.language.LanguageSelector

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
        stringResource(R.string.category_plowing),
        stringResource(R.string.category_sowing),
        stringResource(R.string.category_weeding),
        stringResource(R.string.category_irrigation),
        stringResource(R.string.category_harvesting),
        stringResource(R.string.category_fertilizer),
        stringResource(R.string.category_fence_repair),
        stringResource(R.string.category_planting),
        stringResource(R.string.category_digging),
        stringResource(R.string.category_cutting)
    )

    val categoryItems = listOf(
        stringResource(R.string.category_plowing) to R.drawable.plowing,
        stringResource(R.string.category_sowing) to R.drawable.sowing,
        stringResource(R.string.category_weeding) to R.drawable.weeding,
        stringResource(R.string.category_irrigation) to R.drawable.irrigation,
        stringResource(R.string.category_harvesting) to R.drawable.harvesting,
        stringResource(R.string.category_fertilizer) to R.drawable.fertilizers,
        stringResource(R.string.category_digging) to R.drawable.digging
        // Uncomment if resources available
        // stringResource(R.string.category_fence_repair) to R.drawable.fence_repair,
        // stringResource(R.string.category_planting) to R.drawable.planting,
        // stringResource(R.string.category_cutting) to R.drawable.cutting
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
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            // Top App Bar as first scrollable item
            item {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.home_title), fontSize = 20.sp, modifier = Modifier.padding(start = 8.dp)) },
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
                                contentDescription = stringResource(id = R.string.drawer_open),
                                tint = Color.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    },
                    actions = {
                        LanguageSelector()
                    }
                )
            }

            // Loading
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(500.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }

                }
            }

            // Error
            errorMessage?.let {
                item {
                    Text(
                        stringResource(id = R.string.error_prefix, it),
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
                            "farmer" -> FarmerHomeContent(user, categoryItems, navController)

                            "worker" -> WorkerHomeContent(user, navController)
                        }
                    }
                }
            }

            // Fallback
            if (!isLoading && userData == null && errorMessage == null) {
                item {
                    Text(stringResource(id = R.string.data_load_failed))
                }
            }
        }
    }
}

@Composable
fun FarmerHomeContent(currentUser: AppUser, categoryItems: List<Pair<String, Int>>, navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        PrimaryButton(
            onClick = { navController.navigate("creatework") },
            text = stringResource(id = R.string.post_a_work)
        )

        HorizontalDivider()

        WorkCategorySection(
            title = stringResource(id = R.string.work_categories_title),
            categoryItems = categoryItems,
            onCategoryClick = { category ->
                navController.navigate("creatework/$category")
            }
        )

        HorizontalDivider()

        FarmerDashboardScreen(currentUser, navController)
    }
}

@Composable
fun WorkerHomeContent(currentUser: AppUser, navController: NavHostController) {
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
