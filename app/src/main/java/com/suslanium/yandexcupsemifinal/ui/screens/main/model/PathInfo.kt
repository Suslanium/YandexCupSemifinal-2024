package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

class PathInfo(
    val path: Path,
    val width: Float,
    val color: Color,
    val blendMode: BlendMode,
) {
    fun copy(): PathInfo {
        return PathInfo(
            path = Path().apply { addPath(path) },
            width = width,
            color = color,
            blendMode = blendMode,
        )
    }
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
    state: MainScreenState,
): PathInfo {
    return createPathInfo(
        points = state.newPathPoints,
        widthPx = state.selectedWidthPx,
        color = state.actualColor,
        blendMode = state.blendMode,
    )
}

fun createPathInfo(
    points: List<Offset>,
    widthPx: Float,
    color: Color,
    blendMode: BlendMode,
): PathInfo {
    return PathInfo(
        path = createPathFromPoints(points),
        width = widthPx,
        color = color,
        blendMode = blendMode,
    )
}