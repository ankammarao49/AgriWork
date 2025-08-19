package com.example.agriwork.ui.components

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.agriwork.data.utils.VoiceInputHelper

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkTitleInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    suggestions: List<String>,
    isError: Boolean,
    errorMessage: String,
    description: String = "This is input",
) {
    val context = LocalContext.current
    val activity = context.findActivity() ?: return

    // Use VoiceInputHelper
    val voiceHelper = remember { VoiceInputHelper(activity) }
    val targetScale = if (voiceHelper.isListening.value) 1f + (voiceHelper.rmsLevel.value / 10f) * 0.5f else 1f
    val pulseScale by animateFloatAsState(targetValue = targetScale)

    val filteredSuggestions = remember(value, suggestions) {
        if (value.isNotEmpty()) {
            suggestions.filter {
                it.contains(value, ignoreCase = true) || it.contains("others", ignoreCase = true)
            }.sorted()
        } else {
            suggestions.sorted()
        }
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val isFocused by interactionSource.collectIsFocusedAsState()

    // Track if user typed anything at least once
    var hasTyped by remember { mutableStateOf(false) }

    LaunchedEffect(voiceHelper.recognizedText.value) {
        if (voiceHelper.recognizedText.value.isNotBlank()) {
            onValueChange(voiceHelper.recognizedText.value)
        }
    }

    LaunchedEffect(value) {
        if (!hasTyped && value.isNotEmpty()) {
            hasTyped = true
        }
    }

    // Show error only after the user has typed something
    val showError = isError && hasTyped

    val infoColor = Color.Black
    val successGreen = Color(0xFF43A047)

    val borderColor = when {
        isFocused -> Color.Black
        showError -> MaterialTheme.colorScheme.error
        value.isNotBlank() -> successGreen
        else -> Color(0xffd6d3d1)
    }
    val background = when {
        showError -> Color(0xFFFFF5F5)
        value.isNotBlank() -> Color(0xFFF7FFF5)
        else -> Color(0xfff5f5f4)
    }

    // Cleanup always runs when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            voiceHelper.destroy()
        }
    }


    // Category Field
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                value = value,
                onValueChange = {
                    onValueChange(it)
                    expanded = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = value,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = interactionSource,
                        isError = showError,
                        label = { Text(text = label, color = Color(0xff78716c)) },
                        placeholder = {},
                        leadingIcon = { Icon(icon, contentDescription = "$label icon", tint = Color.Black) },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { voiceHelper.startListening() }, modifier = Modifier.scale(pulseScale)) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = "Voice Input",
                                        tint = if (voiceHelper.isListening.value) Color.Blue else Color.Black
                                    )
                                }
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = if (expanded) "Collapse" else "Expand",
                                        tint = Color.Black
                                    )
                                }
                            }
                        },
                        prefix = {},
                        suffix = {},
                        supportingText = {
                            if (voiceHelper.isListening.value) {
                                Text(
                                    text = "Listening...",
                                    color = Color.Blue,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else if (showError) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = errorMessage,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = description,
                                        color = Color.Black,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        ,
                        contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                        container = {
                            OutlinedTextFieldDefaults.Container(
                                enabled = true,
                                isError = showError && value.isBlank(),
                                interactionSource = interactionSource,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = background,
                                    unfocusedContainerColor = background,
                                    errorContainerColor = background,
                                    focusedBorderColor = borderColor,
                                    unfocusedBorderColor = borderColor,
                                    errorBorderColor = borderColor,
                                    cursorColor = borderColor
                                ),
                                shape = RoundedCornerShape(14.dp),
                                focusedBorderThickness = 3.dp,
                                unfocusedBorderThickness = 2.dp
                            )
                        }
                    )
                }
            )
        }

        // Dropdown with animation
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .width(textFieldSize.width.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0F0F0)
                )
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                    items(filteredSuggestions) { title ->
                        ItemsCategory(title = title) { selected ->
                            onValueChange(selected)
                            expanded = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsCategory(
    title: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
            }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.Black)
    }

}