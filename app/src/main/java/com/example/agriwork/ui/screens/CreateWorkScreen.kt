package com.example.agriwork.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import com.example.agriwork.R
import com.example.agriwork.data.model.AppUser
import com.example.agriwork.data.model.Work
import com.example.agriwork.data.repository.WorkRepository.saveWorkToFirestore
import com.example.agriwork.data.utils.KeyboardDismissWrapper
import com.example.agriwork.getUserFromFirestore
import com.example.agriwork.ui.components.CustomTextField
import com.example.agriwork.ui.components.WorkTitleInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CreateWorkScreen(navController: NavController, category: String = "") {
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

    var currentUser by remember { mutableStateOf<AppUser?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        getUserFromFirestore(
            onSuccess = { currentUser = it },
            onFailure = { error -> errorMessage = error.message }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        KeyboardDismissWrapper {
            when {
                errorMessage != null -> Text(
                    text = stringResource(R.string.error_prefix, errorMessage ?: ""),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(innerPadding)
                )

                currentUser == null -> Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(innerPadding)  // <-- apply scaffold padding here
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        item {
                            IconButton(
                                onClick = { navController.popBackStack() },
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                        item {
                            WorkForm(
                                currentUser = currentUser!!,
                                navController = navController,
                                context = context,
                                category = category,
                                categories = categories,
                                snackbarHostState = snackbarHostState,  // pass snackbar host
                                scope = scope                              // pass coroutine scope
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkForm(
    currentUser: AppUser,
    navController: NavController,
    context: Context,
    category: String,
    categories: List<String>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {

    var workTitle by remember { mutableStateOf(category.ifBlank { "" }) }
    var workTitleError by remember { mutableStateOf(false) }

    var daysRequired by remember { mutableStateOf("") }
    var daysRequiredError by remember { mutableStateOf(false) }

    var acres by remember { mutableStateOf("") }
    var acresError by remember { mutableStateOf(false) }

    var workersNeeded by remember { mutableStateOf("") }
    var workersNeededError by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var workPreview by remember { mutableStateOf<Work?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    )
    {
        Text(
            text = stringResource(R.string.post_work),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Column(verticalArrangement = Arrangement.spacedBy(25.dp)) {

            // Work Title
//                CustomTextField(
//                    label = "Work Title",
//                    value = workTitle,
//                    onValueChange = {
//                        workTitle = it
//                        workTitleError = it.isBlank() || it.length < 3
//                    },
//                    icon = Icons.Default.Work,
//                    description = "What is the name of the work?",
//                    externalError = workTitleError,
//                    externalErrorMessage = when {
//                        workTitle.isBlank() -> "Title cannot be empty"
//                        workTitle.length < 3 -> "Title must have at least 3 characters"
//                        else -> ""
//                    }
//                )
            WorkTitleInput(
                label = stringResource(R.string.work_title),
                value = workTitle,
                onValueChange = {
                    workTitle = it
                    workTitleError = it.isBlank() || it.length < 3
                },
                icon = Icons.Default.Work,
                description = stringResource(R.string.work_title_description),
                suggestions = categories, // <-- List<String> with your autocomplete items
                isError = workTitleError,
                errorMessage = when {
                    workTitle.isBlank() -> stringResource(R.string.title_empty)
                    workTitle.length < 3 -> stringResource(R.string.title_short)
                    else -> ""
                }
            )


            // Days Required
            CustomTextField(
                label = stringResource(R.string.days_required),
                value = daysRequired,
                onValueChange = {
                    daysRequired = it.filter(Char::isDigit)
                    daysRequiredError = daysRequired.isBlank()
                },
                icon = Icons.Default.CalendarToday,
                keyboardType = KeyboardType.Number,
                description = stringResource(R.string.days_required_description),
                externalError = daysRequiredError,
                externalErrorMessage = stringResource(R.string.days_required_error)
            )

            // Acres
            CustomTextField(
                label = stringResource(R.string.acres),
                value = acres,
                onValueChange = {
                    if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                        acres = it
                    }
                    acresError = acres.isBlank()
                },
                icon = Icons.Default.Landscape,
                keyboardType = KeyboardType.Decimal,
                description = stringResource(R.string.acres_description),
                externalError = acresError,
                externalErrorMessage = stringResource(R.string.acres_error)
            )

            // Workers Needed
            CustomTextField(
                label = stringResource(R.string.workers_needed),
                value = workersNeeded,
                onValueChange = {
                    workersNeeded = it.filter(Char::isDigit)
                    workersNeededError = workersNeeded.isBlank()
                },
                icon = Icons.Default.Group,
                keyboardType = KeyboardType.Number,
                description = stringResource(R.string.workers_needed_description),
                externalError = workersNeededError,
                externalErrorMessage = stringResource(R.string.workers_needed_error)
            )
        }

        Button(
            onClick = {
                focusManager.clearFocus()
                if (workTitle.length > 2 && daysRequired.isNotBlank() &&
                    acres.isNotBlank() && workersNeeded.isNotBlank()
                ) {
                    workPreview = Work(
                        farmer = currentUser,
                        workTitle = workTitle,
                        daysRequired = daysRequired.toInt(),
                        acres = acres.toDouble(),
                        workersNeeded = workersNeeded.toInt()
                    )
                    showSheet = true
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.form_incomplete),
                            withDismissAction = true
                        )
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
                .align(Alignment.End),
            shape = RoundedCornerShape(15.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.saving))
            } else {
                Text(stringResource(R.string.submit))
            }
        }
    }


    if (showSheet && workPreview != null) {
        WorkPreviewSheet(
            sheetState = sheetState,
            work = workPreview!!,
            onEdit = { showSheet = false },
            onConfirm = {
                isSaving = true
                scope.launch {
                    saveWorkToFirestore(
                        work = workPreview!!,
                        onSuccess = {
                            isSaving = false
                            showSheet = false
                            scope.launch {
                                snackbarHostState.showSnackbar( message = context.getString(R.string.work_posted_success))
                            }
                        },
                        onFailure = { error ->
                            isSaving = false
                            scope.launch {
                                snackbarHostState.showSnackbar(message = context.getString(R.string.work_posted_failure), error.message ?: "")
                            }
                        }
                    )
                }
            }
        )
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkPreviewSheet(
    sheetState: SheetState,
    work: Work,
    onEdit: () -> Unit,
    onConfirm: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onEdit,
        sheetState = sheetState,
        containerColor = Color.White,
        tonalElevation = 12.dp,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = stringResource(R.string.review),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Card with details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        PreviewRow(Icons.Default.Work, stringResource(R.string.work_title), work.workTitle)
                        PreviewRow(Icons.Default.CalendarToday, stringResource(R.string.days_required), "${work.daysRequired} ${stringResource(R.string.days)}")
                        PreviewRow(Icons.Default.Landscape, stringResource(R.string.acres), "${work.acres} ${stringResource(R.string.acres_unit)}")
                        PreviewRow(Icons.Default.Group, stringResource(R.string.workers_needed), "${work.workersNeeded}")

                    }
                }

                // Buttons
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.edit))
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            onConfirm()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary
        )
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}