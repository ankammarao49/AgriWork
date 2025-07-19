package com.example.agriwork

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreateWorkScreen(navController: NavController, category: String = "") {
    var submittedWork by remember { mutableStateOf<Work?>(null) }
    var currentUser by remember { mutableStateOf<AppUser?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // One-time fetch
    LaunchedEffect(Unit) {
        getUserFromFirestore(
            onSuccess = { user -> currentUser = user },
            onFailure = { error -> errorMessage = error.message }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Close Button Row
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
        }
        when {
            errorMessage != null -> {
                Text("⚠️ Error: $errorMessage", color = MaterialTheme.colorScheme.error)
            }

            currentUser == null -> {
                // Still loading
                Text("Loading user...", style = MaterialTheme.typography.bodyMedium)
            }

            else -> {
                // Show the form once user is loaded
                Column(modifier = Modifier.weight(1f)) {
                    WorkForm(
                        onSubmit = { work ->
                            submittedWork = work
                        },
                        navController = navController,
                        context = context,
                        currentUser = currentUser!!,
                        category = category
                    )
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkForm(
    onSubmit: (Work) -> Unit,
    currentUser: AppUser,
    navController: NavController,
    context: Context,
    category: String
) {
    var workTitle by remember { mutableStateOf(category.ifBlank { "" }) }
    var daysRequired by remember { mutableStateOf("") }
    var acres by remember { mutableStateOf("") }
    var workersNeeded by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }
    var workPreview by remember { mutableStateOf<Work?>(null) }
    val focusManager = LocalFocusManager.current


    // Sheet Content
    if (showSheet && workPreview != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shape = MaterialTheme.shapes.large // Rounded corners
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Review Work Request", style = MaterialTheme.typography.titleLarge)

                Text("🪓 Work Title: ${workPreview!!.workTitle}")
                Text("📅 Days Required: ${workPreview!!.daysRequired}")
                Text("🌾 Acres: ${workPreview!!.acres}")
                Text("👷 Workers Needed: ${workPreview!!.workersNeeded}")

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            showSheet = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit")
                    }

                    Button(
                        onClick = {
                            workPreview?.let { work ->
                                saveWorkToFirestore(
                                    work = work,
                                    onSuccess = {
                                        showSheet = false
                                        Toast.makeText(context, "Work Submitted!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("home") {
                                            popUpTo("createWork") { inclusive = true }
                                        }
                                    },
                                    onFailure = { e ->
                                        showSheet = false
                                        Toast.makeText(context, "❌ Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                )
                            } ?: run {
                                showSheet = false
                                Toast.makeText(context, "⚠️ Something went wrong. Try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        ,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirm")
                    }

                }
            }

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create Work Request", style = MaterialTheme.typography.titleLarge)

        @Composable
        fun inputField(label: String, value: String, onValueChange: (String) -> Unit) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                isError = showError && value.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        inputField("Work Title", workTitle) { workTitle = it.trim() }

        OutlinedTextField(
            value = daysRequired,
            onValueChange = { daysRequired = it.filter { c -> c.isDigit() } },
            label = { Text("Days Required") },
            isError = showError && daysRequired.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = acres,
            onValueChange = {
                acres = it.takeIf { input -> input.matches(Regex("^\\d*\\.?\\d*\$")) } ?: acres
            },
            label = { Text("Acres") },
            isError = showError && acres.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            value = workersNeeded,
            onValueChange = { workersNeeded = it.filter { c -> c.isDigit() } },
            label = { Text("Workers Needed") },
            isError = showError && workersNeeded.isBlank(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (showError) {
            Text(
                "Please fill in all fields correctly.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Button(
            onClick = {
                focusManager.clearFocus(force = true)
                if (
                    workTitle.isNotBlank() &&
                    daysRequired.isNotBlank() &&
                    acres.isNotBlank() &&
                    workersNeeded.isNotBlank()
                ) {
                    val work = Work(
                        farmer = currentUser,
                        workTitle = workTitle.trim(),
                        daysRequired = daysRequired.toIntOrNull() ?: 0,
                        acres = acres.toDoubleOrNull() ?: 0.0,
                        workersNeeded = workersNeeded.toIntOrNull() ?: 0,
                    )
                    workPreview = work
                    showSheet = true
                } else {
                    showError = true
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit")
        }
    }
}

fun saveWorkToFirestore(
    work: Work,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("works") // You can name the collection anything you want
        .add(work)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}