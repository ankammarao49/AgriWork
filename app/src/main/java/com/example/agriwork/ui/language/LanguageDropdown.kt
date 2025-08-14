package com.example.agriwork.ui.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LanguageDropdown(
    currentLanguageIndex: Int,
    onLanguageSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    // List of supported languages
    val languages = listOf("English", "తెలుగు")

    Box {
        Button(onClick = { expanded = !expanded }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "Language Icon",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        expanded = false
                        scope.launch {
                            onLanguageSelected(index) // Save + Apply in parent
                        }
                    }
                )
            }
        }
    }
}
