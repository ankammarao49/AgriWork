package com.example.agriwork

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.data.model.AppUser
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun CreateProfileScreen(
    onProfileCreated: (name: String, location: String, role: String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var role by remember { mutableStateOf<String?>(null) }
    var isDetecting by remember { mutableStateOf(false) }

    // 👇 This flag shows the preview
    var showPreview by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    ProfileInputSection(
                        name = name,
                        onNameChange = { name = it },
                        location = location,
                        onLocationChange = { location = it },
                        onDetectClick = {
                            isDetecting = true
                            location = "Detected Village, District"
                            isDetecting = false
                        },
                        isDetecting = isDetecting,
                        selectedRole = role,
                        onRoleSelected = { role = it }
                    )
                }

                if (showPreview) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("✅ Profile Info:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("👤 Name: $name", fontSize = 16.sp)
                        Text("📍 Location: $location", fontSize = 16.sp)
                        Text("🧑‍🌾 Role: ${role?.replaceFirstChar { it.uppercase() }}", fontSize = 16.sp)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp)) // Space for the button
                }
            }


            SubmitProfileButton(
                name = name,
                location = location,
                role = role,
                onSubmit = {
                    showPreview = true
                    onProfileCreated(name, location, role!!)
                },
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .navigationBarsPadding()
            )

        }
    }
}

@Composable
fun ProfileInputSection(
    name: String,
    onNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    onDetectClick: () -> Unit,
    isDetecting: Boolean,
    selectedRole: String?,
    onRoleSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text(
            text = "Complete your profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Your Location") },
            modifier = Modifier.fillMaxWidth()
        )

        DetectLocationButton(
            isDetecting = isDetecting,
            onClick = onDetectClick
        )

        Text(
            text = "Choose your role",
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )

        RoleSelectionRow(
            selectedRole = selectedRole,
            onRoleSelected = onRoleSelected
        )
    }
}

@Composable
fun DetectLocationButton(isDetecting: Boolean, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(if (isDetecting) "Detecting..." else "Detect Location")
    }
}

@Composable
fun RoleSelectionRow(
    selectedRole: String?,
    onRoleSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            RoleChip(
                title = "Farmer",
                isSelected = selectedRole == "farmer",
                onClick = { onRoleSelected("farmer") }
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            RoleChip(
                title = "Worker",
                isSelected = selectedRole == "worker",
                onClick = { onRoleSelected("worker") }
            )
        }
    }
}

@Composable
fun RoleChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(title, maxLines = 1)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp), // Less rounded than pill shape
    )
}

@Composable
fun SubmitProfileButton(
    name: String,
    location: String,
    role: String?,
    onSubmit: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    PrimaryButton(
        text = "Continue",
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                when {
                    name.isBlank() -> snackbarHostState.showSnackbar("Please enter your name")
                    location.isBlank() -> snackbarHostState.showSnackbar("Please enter your location")
                    role == null -> snackbarHostState.showSnackbar("Please select your role")
                    else -> {
                        val userPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: return@launch

                        val user = AppUser(
                            name = name,
                            location = location,
                            role = role,
                            mobileNumber = userPhone
                        )

                        saveUserToFirestore(
                            user = user,
                            onSuccess = {
                                onSubmit()
                            },
                            onFailure = {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Something went wrong. Please try again.")
                                }
                            }

                        )
                    }
                }
            }
        }
    )
}
