package com.suslanium.yandexcupsemifinal.ui.screens.main.model

sealed interface MainScreenEffect {

    data object ChooseFileLocation : MainScreenEffect

    data object ShowEmptyFrameAmountError : MainScreenEffect

    data object ShowInvalidFrameAmountError : MainScreenEffect

    data object ShowGifExportError : MainScreenEffect
}