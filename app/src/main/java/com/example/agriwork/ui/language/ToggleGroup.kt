package com.example.agriwork.ui.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private val languages = listOf("English", "Telugu")

@Composable
fun ToggleGroup(
    selectedPosition: Int,
    onClick: (Int) -> Unit
) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape)
            .border(1.dp, Color(0xFFAAAAAA), shape)
    ) {
        languages.forEachIndexed { index, element ->
            Text(
                text = element,
                color = if (index != selectedPosition) Color.Black else Color.White,
                modifier = Modifier
                    .align(CenterVertically)
                    .background(
                        if (index == selectedPosition) MaterialTheme.colors.primary else Color.Transparent
                    )
                    .clickable(
                        onClick = { onClick(index) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
