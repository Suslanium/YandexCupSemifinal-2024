package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

data class MainScreenState(
    val selectedColor: Color,
    val selectedWidthPx: Float,
    val interactionType: InteractionType,
    val colorSelectorState: ColorSelectorState,
    private val isRedoAvailableProvider: () -> Boolean,
    private val isUndoAvailableProvider: () -> Boolean,
    private val pathsProvider: () -> List<PathInfo>,
    private val currentPathPointsProvider: () -> List<Offset>,
) {
    val actualColor: Color
        get() = when (interactionType) {
            InteractionType.Drawing -> selectedColor
            InteractionType.Erasing -> Color.Transparent
        }

    val blendMode: BlendMode
        get() = when (interactionType) {
            InteractionType.Drawing -> DrawScope.DefaultBlendMode
            InteractionType.Erasing -> BlendMode.Clear
        }

    val isUndoAvailable: Boolean
        get() = isUndoAvailableProvider()

    val isRedoAvailable: Boolean
        get() = isRedoAvailableProvider()

    val paths: List<PathInfo>
        get() = pathsProvider()

    val currentPathPoints: List<Offset>
        get() = currentPathPointsProvider()
}