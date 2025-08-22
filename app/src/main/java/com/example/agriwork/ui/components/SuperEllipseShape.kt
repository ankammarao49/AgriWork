package com.example.agriwork.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * A true superellipse (squircle) shape using the Lamé curve formula.
 *
 * @param n Controls roundness: 2 = ellipse, 4 = squircle, higher = squarer.
 */
class SuperEllipseShape(private val n: Double = 4.0) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val steps = 200 // number of curve segments → smoother if higher

        val a = size.width / 2f   // semi-major axis
        val b = size.height / 2f  // semi-minor axis
        val cx = size.width / 2f
        val cy = size.height / 2f

        for (i in 0..steps) {
            val theta = (2.0 * Math.PI * i / steps)
            val cosT = cos(theta)
            val sinT = sin(theta)

            // Superellipse radius formula
            val r = 1.0 / (
                    abs(cosT).pow(n) + abs(sinT).pow(n)
                    ).pow(1.0 / n)

            val x = cx + (a * r * cosT).toFloat()
            val y = cy + (b * r * sinT).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        path.close()
        return Outline.Generic(path)
    }
}

@Composable
fun SuperEllipseCardDemo() {
    Box(
        modifier = Modifier
            .size(200.dp, 120.dp)
            .background(Color(0xFFECECEC), shape = SuperEllipseShape(4.0)) // squircle
            .padding(16.dp)
    ) {
        Text("Hello Squircle 👋")
    }
}
