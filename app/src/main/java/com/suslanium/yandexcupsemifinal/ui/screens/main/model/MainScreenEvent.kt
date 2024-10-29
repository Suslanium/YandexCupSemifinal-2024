package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

sealed interface MainScreenEvent {

    data class InteractionTypeChanged(val interactionType: InteractionType) : MainScreenEvent

    data class NewPathStarted(val startPoint: Offset) : MainScreenEvent

    data class PathPointAdded(val point: Offset) : MainScreenEvent

    data object PathFinished : MainScreenEvent

    data object ColorSelectorClicked : MainScreenEvent

    data object ExtendedColorSelectorClicked : MainScreenEvent

    data class ColorSelected(val color: Color) : MainScreenEvent

    data object Undo : MainScreenEvent

    data object Redo : MainScreenEvent
}