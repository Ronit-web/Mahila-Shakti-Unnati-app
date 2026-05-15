package com.mahilashakti.unnati.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mahilashakti.unnati.viewmodel.ChartDataPoint

@Composable
fun ModernLineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.animateTo(1f, tween(1000))
    }

    Canvas(modifier = modifier.padding(16.dp)) {
        if (data.isEmpty()) return@Canvas

        val spacing = size.width / (data.size - 1)
        val maxVal = data.maxOf { it.value }.coerceAtLeast(1f)
        val points = data.mapIndexed { index, point ->
            Offset(
                x = index * spacing,
                y = size.height - (point.value / maxVal * size.height * animationProgress.value)
            )
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val p0 = points[i - 1]
                val p1 = points[i]
                cubicTo(
                    (p0.x + p1.x) / 2, p0.y,
                    (p0.x + p1.x) / 2, p1.y,
                    p1.x, p1.y
                )
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Fill area under the line with gradient
        val fillPath = Path().apply {
            addPath(path)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}

@Composable
fun ModernBarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.secondary
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.animateTo(1f, tween(1000))
    }

    Canvas(modifier = modifier.padding(16.dp)) {
        if (data.isEmpty()) return@Canvas

        val barWidth = size.width / (data.size * 2)
        val maxVal = data.maxOf { it.value }.coerceAtLeast(1f)
        
        data.forEachIndexed { index, point ->
            val left = index * (size.width / data.size) + barWidth / 2
            val top = size.height - (point.value / maxVal * size.height * animationProgress.value)
            
            drawRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, size.height - top)
            )
        }
    }
}
