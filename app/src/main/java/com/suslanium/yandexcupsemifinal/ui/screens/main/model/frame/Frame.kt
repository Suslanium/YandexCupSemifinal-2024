package com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState

interface UiFrame {
    val paths: List<PathInfo>
    val redoStack: List<PathInfo>
}

class Frame() : UiFrame {
    val mutablePaths: SnapshotStateList<PathInfo> = mutableStateListOf()
    val mutableRedoStack: SnapshotStateList<PathInfo> = mutableStateListOf()

    override val paths: List<PathInfo>
        get() = mutablePaths

    override val redoStack: List<PathInfo>
        get() = mutableRedoStack

    constructor(paths: List<PathInfo>) : this() {
        mutablePaths.addAll(paths)
    }
}

fun Frame.addPath(state: MainScreenState) {
    val pathInfo = createPathInfo(state)
    mutablePaths.add(pathInfo)
    mutableRedoStack.clear()
}

fun Frame.undo() {
    mutablePaths.removeLastOrNull()?.let { path ->
        mutableRedoStack.add(path)
    }
}

fun Frame.redo() {
    mutableRedoStack.removeLastOrNull()?.let { path ->
        mutablePaths.add(path)
    }
}

fun Frame.copy(): Frame {
    val newFrame = Frame()
    newFrame.mutablePaths.addAll(mutablePaths.map { it.copy() })
    newFrame.mutableRedoStack.addAll(mutableRedoStack.map { it.copy() })
    return newFrame
}

fun Frame.toBitmap(width: Int, height: Int): Bitmap {
    val bitmap = ImageBitmap(width, height, hasAlpha = false)
    Canvas(bitmap).apply {
        drawRect(
            paint = Paint().apply {
                color = Color.White
            },
            left = 0f,
            top = 0f,
            right = width.toFloat(),
            bottom = height.toFloat(),
        )
        val checkPoint = nativeCanvas.saveLayer(null, null)
        for (path in paths) {
            drawPath(
                path = path.path,
                paint = Paint().apply {
                    color = path.color
                    strokeWidth = path.width
                    strokeCap = StrokeCap.Round
                    blendMode = path.blendMode
                    style = PaintingStyle.Stroke
                }
            )
        }
        nativeCanvas.restoreToCount(checkPoint)
    }
    return bitmap.asAndroidBitmap()
}