package com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.generator

import androidx.compose.ui.graphics.Color
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.Frame

class Point(
    var x: Float,
    var y: Float,
)

class Line(
    var firstPoint: Point,
    var secondPoint: Point,
)

abstract class FrameGenerator(
    protected val canvasWidth: Int,
    protected val canvasHeight: Int,
    protected val color: Color,
    protected val strokeWidthPx: Float,
) {

    abstract fun generateFrames(amount: Int): List<Frame>
}