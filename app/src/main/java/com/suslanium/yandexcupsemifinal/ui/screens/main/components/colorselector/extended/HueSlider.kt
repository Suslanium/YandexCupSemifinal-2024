package com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.extended

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun HueSlider(
    hue: Float,
    onHueChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var width = 0f
    Canvas(
        modifier = modifier.draggableClickable(
            onDrag = { delta ->
                onHueChanged((hue + delta * 360f / width).coerceIn(0f, 360f))
            },
            onClick = { offset ->
                onHueChanged((offset * 360f / width).coerceIn(0f, 360f))
            },
        ),
    ) {
        width = size.width
        var currentHue = 0f
        for (i in 0 until size.width.toInt()) {
            val color = Color.hsv(currentHue, 1f, 1f)
            drawLine(
                color,
                Offset(i.toFloat(), 0f),
                Offset(i.toFloat(), size.height),
                strokeWidth = 0f,
            )
            currentHue += 360f / size.width.toInt()
        }

        val hueOffset = hue * size.width / 360f
        drawCircle(
            Color.White,
            radius = size.height / 2,
            center = Offset(hueOffset, size.height / 2),
            style = Stroke(
                width = 1.dp.toPx()
            )
        )
    }
}