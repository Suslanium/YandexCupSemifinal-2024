package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class PathInfo(
    val path: Path,
    val width: Float,
    val color: Color,
    val blendMode: BlendMode,
)