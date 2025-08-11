package com.example.agriwork.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    externalError: Boolean = false,
    externalErrorMessage: String = "",
    description: String = "This is input",
    keyboardType: KeyboardType = KeyboardType.Text,

) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Track if user typed anything at least once
    var hasTyped by remember { mutableStateOf(false) }

    LaunchedEffect(value) {
        if (!hasTyped && value.isNotEmpty()) {
            hasTyped = true
        }
    }

    // Show error only after the user has typed something
    val showError = externalError && hasTyped

    val infoColor = Color.Black
    val successGreen = Color(0xFF43A047)

    val borderColor = when {
        showError -> MaterialTheme.colorScheme.error
        isFocused -> Color.Black
        value.isNotBlank() -> successGreen
        else -> Color(0xffd6d3d1)
    }
    val background = when {
        showError -> Color(0xFFFFF5F5)
        value.isNotBlank() -> Color(0xFFF7FFF5)
        else -> Color(0xfff5f5f4)
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
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
                    AnimatedVisibility(
                        visible = value.isNotBlank() && !showError,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Valid input",
                            tint = successGreen
                        )
                    }
                },
                prefix = {},
                suffix = {},
                supportingText = {
                    if (showError) {
                        Text(
                            text = externalErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    else {
                        androidx.compose.foundation.layout.Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = description,
                                color = infoColor,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
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
