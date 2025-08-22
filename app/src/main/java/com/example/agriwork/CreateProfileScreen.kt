package com.example.agriwork

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.data.model.AppUser
import com.example.agriwork.data.model.MultiLangField
import com.example.agriwork.ui.components.CustomTextField
import com.example.agriwork.ui.components.PrimaryButton
import com.example.agriwork.ui.components.SuperEllipseShape
import com.example.agriwork.ui.language.LanguageSelector
import com.example.agriwork.ui.language.translateText
import com.example.agriwork.ui.language.transliterateText
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.nl.translate.TranslateLanguage
import detectLocation
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun CreateProfileScreen(
    onProfileCreated: (name: String, location: String, role: String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var role by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isDetecting by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .padding(24.dp)
    ) {
        Text(
            text = "Create Profile",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        LanguageSelector()

        Spacer(modifier = Modifier.height(12.dp))


//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(8.dp)
//                .clip(RoundedCornerShape(50))
//                .background(Color(0xFFB2EBF2)) // track
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(0.5f) // progress
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(50))
//                    .background(Color(0xFF4DD0E1))
//            )
//        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            pageSpacing = 16.dp,
            userScrollEnabled = false
        ) { page ->
            // how far the page is from the current position
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // fade out as you swipe away
                        alpha = 1f - pageOffset.coerceIn(0f, 1f)
                        // scale down slightly when swiping away
                        scaleX = 0.9f + (0.1f * (1 - pageOffset.coerceIn(0f, 1f)))
                        scaleY = 0.9f + (0.1f * (1 - pageOffset.coerceIn(0f, 1f)))
                    }
            ) {
                when (page) {
                    0 -> RoleSelectionPage(
                        selectedRole = role,
                        onRoleSelected = { role = it },
                        onNext = {
                            if (role != null) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = 1,
                                    )
                                }


                            }
                        }
                    )

                    1 -> NameLocationPage(
                        name = name,
                        onNameChange = { name = it },
                        location = location,
                        onLocationChange = { location = it },
                        isDetecting = isDetecting,
                        onDetectingChange = { isDetecting = it },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        onSubmit = {
                            if (name.isNotBlank() && location.isNotBlank() && role != null) {
                                onProfileCreated(name, location, role!!)
                            }
                        }
                    )
                }

            }
        }
        Spacer(modifier = Modifier.height(16.dp))

// Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous Button
            if (pagerState.currentPage > 0) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .width(140.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous step",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Previous", color = Color.White)
                }
            } else {
                Spacer(modifier = Modifier.width(140.dp)) // keep spacing so layout is balanced
            }

            // Next / Submit Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage == 0) {
                            pagerState.animateScrollToPage(1)
                        } else {
                            when {
                                name.isBlank() -> snackbarHostState.showSnackbar("Please enter your name")
                                location.isBlank() -> snackbarHostState.showSnackbar("Please enter your location")
                                role == null -> snackbarHostState.showSnackbar("Please select your role")
//                                else -> {
//                                    val userPhone =
//                                        FirebaseAuth.getInstance().currentUser?.phoneNumber
//                                            ?: return@launch
//
//                                    val user = AppUser(
//                                        name = name,
//                                        location = location,
//                                        role = role!!,
//                                        mobileNumber = userPhone
//                                    )
//
//                                    saveUserToFirestore(
//                                        user = user,
//                                        onSuccess = { onProfileCreated(name, location, role!!) },
//                                        onFailure = {
//                                            coroutineScope.launch {
//                                                snackbarHostState.showSnackbar("Something went wrong. Please try again.")
//                                            }
//                                        }
//                                    )
//                                }
                            }
                        }
                    }
                },
                enabled = if (pagerState.currentPage == 0) role != null else name.isNotBlank() && location.isNotBlank(),
                modifier = Modifier
                    .width(140.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text(
                    if (pagerState.currentPage == 0) "Next" else "Submit",
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (pagerState.currentPage == 0)
                        Icons.AutoMirrored.Filled.ArrowForward
                    else
                        Icons.Default.Check,
                    contentDescription = if (pagerState.currentPage == 0) "Next step" else "Submit profile",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // 🔹 Preview Section (only if data is filled)
        if (name.isNotBlank() && location.isNotBlank() && role != null) {
            ProfilePreviewCard(name, location, role!!)
        }
    }
}

@Composable
fun ProfilePreviewCard(name: String, location: String, role: String) {
    var previewName by remember { mutableStateOf(MultiLangField(name, "", "", "")) }
    var previewLocation by remember { mutableStateOf(MultiLangField(location, "", "", "")) }
    var previewRole by remember { mutableStateOf(MultiLangField(role, "", "", "")) }

    // 🔹 Translate whenever inputs change
    LaunchedEffect(name, location, role) {
        if (name.isNotBlank()) {
            // Telugu
            transliterateText(name, TranslateLanguage.ENGLISH, TranslateLanguage.TELUGU) {
                previewName = previewName.copy(te = it)
            }
            // Hindi
            transliterateText(name, TranslateLanguage.ENGLISH, TranslateLanguage.HINDI) {
                previewName = previewName.copy(hi = it)
            }
            // Tamil
            transliterateText(name, TranslateLanguage.ENGLISH, TranslateLanguage.TAMIL) {
                previewName = previewName.copy(ta = it)
            }
        }

        if (location.isNotBlank()) {
            translateText(location, TranslateLanguage.ENGLISH, TranslateLanguage.TELUGU) {
                previewLocation = previewLocation.copy(te = it)
            }
            translateText(location, TranslateLanguage.ENGLISH, TranslateLanguage.HINDI) {
                previewLocation = previewLocation.copy(hi = it)
            }
            translateText(location, TranslateLanguage.ENGLISH, TranslateLanguage.TAMIL) {
                previewLocation = previewLocation.copy(ta = it)
            }
        }

        if (role.isNotBlank()) {
            translateText(role, TranslateLanguage.ENGLISH, TranslateLanguage.TELUGU) {
                previewRole = previewRole.copy(te = it)
            }
            translateText(role, TranslateLanguage.ENGLISH, TranslateLanguage.HINDI) {
                previewRole = previewRole.copy(hi = it)
            }
            translateText(role, TranslateLanguage.ENGLISH, TranslateLanguage.TAMIL) {
                previewRole = previewRole.copy(ta = it)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Profile Preview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            PreviewJsonBlock("👤 Name", previewName)
            PreviewJsonBlock("📍 Location", previewLocation)
            PreviewJsonBlock("💼 Role", previewRole)
        }
    }
}

@Composable
fun PreviewJsonBlock(label: String, text: MultiLangField) {
    Text(
        "$label → { \"en\": \"${text.en}\", \"te\": \"${text.te}\", \"hi\": \"${text.hi}\", \"ta\": \"${text.ta}\" }"
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
fun RoleSelectionPage(
    selectedRole: String?,
    onRoleSelected: (String) -> Unit,
    onNext: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top= 24.dp)
        )
        {
            // Section title
            Text(
                text = "Choose Role",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Role options
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                RoleOptionCard(
                    title = "Farmer",
                    isSelected = selectedRole == "Farmer",
                    selectedColor = Color(0xFF388E3C),
                    onClick = { onRoleSelected("Farmer") },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )

                RoleOptionCard(
                    title = "Worker",
                    isSelected = selectedRole == "Worker",
                    selectedColor = Color(0xFF1976D2),
                    onClick = { onRoleSelected("Worker") },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        }

        // Next button pinned at bottom
//        Row(
//            modifier = Modifier
//                .align(Alignment.BottomEnd),
//            horizontalArrangement = Arrangement.End
//        ) {
//            Button(
//                onClick = onNext,
//                modifier = Modifier
//                    .width(140.dp)
//                    .height(56.dp),
//                shape = RoundedCornerShape(20.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
//            ) {
//                Text("Next", color = Color.White)
//                Spacer(Modifier.width(8.dp))
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                    contentDescription = "Next step",
//                    tint = Color.White
//                )
//            }
//        }
    }
}

@Composable
private fun RoleOptionCard(
    title: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // scale animation for press feedback
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        label = "scaleAnim"
    )

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(SuperEllipseShape(15.0)) // 🔹 squircle instead of rounded rect
            .clickable(
                onClick = onClick,
                onClickLabel = "Select $title",
                interactionSource = interactionSource,
                indication = null,
            ),
        shape = SuperEllipseShape(15.0), // 🔹 card shape set to squircle
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else Color(0xFFE0E0E0)
        ),
        border = if (!isSelected) BorderStroke(2.dp, Color.DarkGray.copy(alpha = 0.6f)) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color.Black
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
        }
    }
}


@Composable
fun NameLocationPage(
    name: String,
    onNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    isDetecting: Boolean,
    onDetectingChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current

    // Permission launcher for location
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onDetectingChange(true)
            detectLocation(
                context,
                onLocationDetected = {
                    onLocationChange(it)
                    onDetectingChange(false)
                },
                onError = {
                    onLocationChange("")
                    onDetectingChange(false)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        CustomTextField(
            label = "Your Name",
            value = name,
            onValueChange = onNameChange,
            icon = Icons.Default.Person,
            keyboardType = KeyboardType.Text,
            description = "Enter your full name",
            externalError = name.isBlank(),
            externalErrorMessage = "Name cannot be empty"
        )

        CustomTextField(
            label = "Your Location",
            value = location,
            onValueChange = onLocationChange,
            icon = Icons.Default.LocationOn,
            keyboardType = KeyboardType.Text,
            description = "Enter your city or allow detection",
            externalError = location.isBlank(),
            externalErrorMessage = "Location cannot be empty"
        )

        DetectLocationButton(
            isDetecting = isDetecting,
            onClick = {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
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

//@Composable
//fun SubmitProfileButton(
//    name: String,
//    location: String,
//    role: String?,
//    onSubmit: () -> Unit,
//    snackbarHostState: SnackbarHostState,
//    modifier: Modifier = Modifier
//) {
//    val coroutineScope = rememberCoroutineScope()
//
//    PrimaryButton(
//        text = "Continue",
//        modifier = modifier,
//        onClick = {
//            coroutineScope.launch {
//                when {
//                    name.isBlank() -> snackbarHostState.showSnackbar("Please enter your name")
//                    location.isBlank() -> snackbarHostState.showSnackbar("Please enter your location")
//                    role == null -> snackbarHostState.showSnackbar("Please select your role")
//                    else -> {
//                        val userPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: return@launch
//
//                        val user = AppUser(
//                            name = name,
//                            location = location,
//                            role = role,
//                            mobileNumber = userPhone
//                        )
//
//                        saveUserToFirestore(
//                            user = user,
//                            onSuccess = {
//                                onSubmit()
//                            },
//                            onFailure = {
//                                coroutineScope.launch {
//                                    snackbarHostState.showSnackbar("Something went wrong. Please try again.")
//                                }
//                            }
//
//                        )
//                    }
//                }
//            }
//        }
//    )
//}
//
