package com.example.agriwork.ui.language

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    val languages = listOf("English", "తెలుగు", "हिन्दी")

    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Change Language",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            languages.forEachIndexed { index, language ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = language,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (currentLanguageIndex == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        scope.launch {
                            onLanguageSelected(index)
                        }
                    },
                    trailingIcon = {
                        if (currentLanguageIndex == index) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
