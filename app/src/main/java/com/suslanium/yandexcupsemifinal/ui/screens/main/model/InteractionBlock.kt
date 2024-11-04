package com.suslanium.yandexcupsemifinal.ui.screens.main.model

sealed interface InteractionBlock {

    data object FrameGeneration : InteractionBlock

    data object GifSaving : InteractionBlock

    data object FrameSelect : InteractionBlock

    data object Playback : InteractionBlock

    data object None : InteractionBlock
}