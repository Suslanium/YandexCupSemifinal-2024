package com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.extended

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SaturationSlider(
    hue: Float,
    saturation: Float,
    modifier: Modifier = Modifier,
    onSaturationChanged: (Float) -> Unit,
) {
    var width = 0f
    val brush = Brush.horizontalGradient(listOf(Color.White, Color.hsv(hue, 1f, 1f)))
    Canvas(
        modifier = modifier.draggableClickable(
            onDrag = { delta ->
                onSaturationChanged((saturation + delta / width).coerceIn(0f, 1f))
            },
            onClick = { offset ->
                onSaturationChanged((offset / width).coerceIn(0f, 1f))
            },
        ),
    ) {
        width = size.width
        drawRect(brush = brush)

        val saturationOffset = saturation * size.width
        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(saturationOffset, size.height / 2),
            style = Stroke(
                width = 1.dp.toPx()
            )
        )
    }
}