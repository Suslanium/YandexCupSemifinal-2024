package com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.generator

import androidx.compose.ui.graphics.Color

enum class FrameGeneratorType {
    KochSnowflake,
    AntiKochSnowflake,
}

fun createFrameGenerator(
    type: FrameGeneratorType,
    canvasWidth: Int,
    canvasHeight: Int,
    color: Color,
    strokeWidthPx: Float,
): FrameGenerator {
    return when (type) {
        FrameGeneratorType.KochSnowflake -> {
            KochSnowflakeGenerator(
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = color,
                strokeWidthPx = strokeWidthPx,
                antiSnowflake = false,
            )
        }
        FrameGeneratorType.AntiKochSnowflake -> {
            KochSnowflakeGenerator(
                canvasWidth = canvasWidth,
                canvasHeight = canvasHeight,
                color = color,
                strokeWidthPx = strokeWidthPx,
                antiSnowflake = true,
            )
        }
    }
}