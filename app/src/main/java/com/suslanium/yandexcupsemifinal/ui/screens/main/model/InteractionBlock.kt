package com.suslanium.yandexcupsemifinal.ui.screens.main.model

sealed interface InteractionBlock {

    data object Playback : InteractionBlock

    data object None : InteractionBlock

}