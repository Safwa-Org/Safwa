package com.tasneem.safwa.core.shared_component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun SafwaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val width = size.toPx()
        val height = size.toPx()
        
        val cornerRadius = width * 0.35f
        drawRoundRect(
            color = containerColor,
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        val centerX = width / 2
        val centerY = height / 2
        val radius = width * 0.2f
        
        val path = Path().apply {
            val a = radius * 0.866f
            val b = radius * 0.5f
            
            moveTo(centerX, centerY - radius)
            lineTo(centerX + a, centerY - b)
            lineTo(centerX + a, centerY + b)
            lineTo(centerX, centerY + radius)
            lineTo(centerX - a, centerY + b)
            lineTo(centerX - a, centerY - b)
            close()
        }
        
        drawPath(
            path = path,
            color = iconColor,
            style = Stroke(width = width * 0.03f)
        )
        
        val diamondPath = Path().apply {
            val dRadius = radius * 0.4f
            moveTo(centerX, centerY - dRadius)
            lineTo(centerX + dRadius, centerY)
            lineTo(centerX, centerY + dRadius)
            lineTo(centerX - dRadius, centerY)
            close()
        }
        drawPath(
            path = diamondPath,
            color = iconColor
        )
    }
}
