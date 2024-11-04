package com.suslanium.yandexcupsemifinal.ui.screens.main.model

sealed interface AdditionalToolsState {

    val isColorSelectorVisible: Boolean
        get() = this is ColorSelector

    val isColorSelectorExpanded: Boolean
        get() = (this as? ColorSelector)?.isExpanded ?: false

    data object Hidden : AdditionalToolsState

    data class ColorSelector(val isExpanded: Boolean) : AdditionalToolsState

    data object WidthSelector : AdditionalToolsState

    data object TopPopupMenu : AdditionalToolsState

    data object SpeedSelector : AdditionalToolsState

    data object FrameGenerationDialog : AdditionalToolsState
}