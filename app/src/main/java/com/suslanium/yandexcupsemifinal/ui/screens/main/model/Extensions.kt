package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

@Composable
fun InteractionType.iconColor(iconType: InteractionType) =
    if (this == iconType) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

fun createPathFromPoints(currentPathPoints: Iterable<Offset>): Path {
    return Path().apply {
        var prevPoint: Offset? = null
        currentPathPoints.forEach { point ->
            prevPoint?.let { prevPoint ->
                quadraticBezierTo(
                    prevPoint.x,
                    prevPoint.y,
                    (prevPoint.x + point.x) / 2,
                    (prevPoint.y + point.y) / 2
                )
            } ?: moveTo(point.x, point.y)
            prevPoint = point
        }
    }
}

fun createPathInfo(
    currentPathPoints: Iterable<Offset>,
    state: MainScreenState,
): PathInfo {
    return PathInfo(
        path = createPathFromPoints(currentPathPoints),
        width = state.selectedWidthPx,
        color = state.actualColor,
        blendMode = state.blendMode,
    )
}