package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

data class MainScreenState(
    val selectedColor: Color,
    val selectedWidthPx: Float,
    val selectedPlaybackFps: Int,
    val interactionType: InteractionType,
    val interactionBlock: InteractionBlock,
    val additionalToolsState: AdditionalToolsState,
    val frames: LongDoublyLinkedList<UiFrame>,
    val currentFrameIndex: Long,
    val newPathPoints: List<Offset>,
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
        get() = frames[currentFrameIndex].paths.isNotEmpty()

    val isRedoAvailable: Boolean
        get() = frames[currentFrameIndex].redoStack.isNotEmpty()

    val isFrameDeletionAvailable: Boolean
        get() = currentFrameIndex > 0

    val isPlayPauseAvailable: Boolean
        get() = interactionBlock == InteractionBlock.None && frames.size > 1 || interactionBlock == InteractionBlock.Playback

}