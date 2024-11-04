package com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.generator

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.Frame
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.PathInfo
import kotlin.math.cos
import kotlin.math.sin

class StartingTriangle(
    val lines: List<Line>,
    val zoomPoint: Point,
)

class ZoomInfo(
    private val zoomPoint: Point,
    private val maxZoomPointPositionY: Float,
) {
    fun shouldIterate(): Boolean {
        return zoomPoint.y >= maxZoomPointPositionY
    }
}

inline fun <T> action(first: T, second: T, action: (T) -> Unit) {
    action(first)
    action(second)
}

class KochSnowflakeGenerator(
    canvasWidth: Int,
    canvasHeight: Int,
    color: Color,
    strokeWidthPx: Float,
    private val antiSnowflake: Boolean,
) : FrameGenerator(canvasWidth, canvasHeight, color, strokeWidthPx) {

    private companion object {
        private const val ANGLE = -Math.PI / 3
        private const val PRE_ZOOM_ITERATIONS_COUNT = 5
        private const val ZOOM_FACTOR_PER_FRAME = 1.03f
    }

    private fun calculatePeak(firstPoint: Point, secondPoint: Point): Point {
        val deltaX = secondPoint.x - firstPoint.x
        val deltaY = secondPoint.y - firstPoint.y
        val peakX = firstPoint.x + deltaX * cos(ANGLE) - deltaY * sin(ANGLE)
        val peakY = firstPoint.y + deltaX * sin(ANGLE) + deltaY * cos(ANGLE)
        return Point(peakX.toFloat(), peakY.toFloat())
    }

    private fun createInitTriangle(): StartingTriangle {
        val firstPoint = Point(canvasWidth / 6f, canvasHeight * 2 / 3f)
        val secondPoint = Point(canvasWidth * 5 / 6f, canvasHeight * 2 / 3f)
        val thirdPoint = calculatePeak(firstPoint, secondPoint)
        return if (antiSnowflake) {
            //Anti-clockwise triangle winding
            StartingTriangle(
                listOf(
                    Line(firstPoint, secondPoint),
                    Line(secondPoint, thirdPoint),
                    Line(thirdPoint, firstPoint),
                ),
                thirdPoint
            )
        } else {
            //Clockwise triangle winding
            StartingTriangle(
                listOf(
                    Line(firstPoint, thirdPoint),
                    Line(thirdPoint, secondPoint),
                    Line(secondPoint, firstPoint),
                ),
                thirdPoint
            )
        }
    }

    private fun MutableList<Line>.performKochTransformation(zoomPoint: Point): ZoomInfo {
        val newLines = mutableListOf<Line>()
        var topZoomPoint: Point? = null
        var bottomZoomPoint: Point? = null

        forEach { line ->
            //Actual transformation
            val deltaX = line.secondPoint.x - line.firstPoint.x
            val deltaY = line.secondPoint.y - line.firstPoint.y
            val firstThird = Point(
                line.firstPoint.x + deltaX / 3,
                line.firstPoint.y + deltaY / 3,
            )
            val secondThird = Point(
                line.firstPoint.x + deltaX * 2 / 3,
                line.firstPoint.y + deltaY * 2 / 3,
            )
            val peak = calculatePeak(firstThird, secondThird)
            newLines.add(Line(line.firstPoint, firstThird))
            newLines.add(Line(firstThird, peak))
            newLines.add(Line(peak, secondThird))
            newLines.add(Line(secondThird, line.secondPoint))

            //Zoom info calculations
            if (!antiSnowflake) {
                if (topZoomPoint == null || peak.y < (topZoomPoint?.y ?: 0f)) {
                    topZoomPoint = peak
                }
                action(line.firstPoint, line.secondPoint) { point ->
                    if (point.y > zoomPoint.y &&
                        (bottomZoomPoint == null || point.y < (bottomZoomPoint?.y ?: 0f))
                    ) {
                        bottomZoomPoint = point
                    }
                }
            } else {
                action(line.firstPoint, line.secondPoint) { point ->
                    if (point.y > zoomPoint.y &&
                        (bottomZoomPoint == null || point.y < (bottomZoomPoint?.y ?: 0f))) {
                        bottomZoomPoint = point
                    }
                }
                action(firstThird, secondThird) { point ->
                    if (topZoomPoint == null || point.y < (topZoomPoint?.y ?: 0f)) {
                        topZoomPoint = point
                    }
                }
            }
        }
        clear()
        addAll(newLines)

        return ZoomInfo(
            checkNotNull(topZoomPoint),
            checkNotNull(bottomZoomPoint).y,
        )
    }

    private fun MutableList<Line>.zoomTowards(point: Point, zoomFactor: Float) {
        forEach { line ->
            line.firstPoint.x = (line.firstPoint.x - point.x) * zoomFactor + point.x
            line.firstPoint.y = (line.firstPoint.y - point.y) * zoomFactor + point.y
            line.secondPoint.x = (line.secondPoint.x - point.x) * zoomFactor + point.x
            line.secondPoint.y = (line.secondPoint.y - point.y) * zoomFactor + point.y
        }
    }

    private fun MutableList<Line>.removeOutOfBoundsLines() {
        val newLines = mutableListOf<Line>()
        forEach { line ->
            if ((line.firstPoint.x in 0f..canvasWidth.toFloat() &&
                        line.firstPoint.y in 0f..canvasHeight.toFloat()) ||
                (line.secondPoint.x in 0f..canvasWidth.toFloat() &&
                        line.secondPoint.y in 0f..canvasHeight.toFloat())
            ) {
                newLines.add(line)
            }
        }
        clear()
        addAll(newLines)
    }

    private fun List<Line>.createFrame(): Frame {
        val paths = map { line ->
            PathInfo(
                path = Path().apply {
                    moveTo(line.firstPoint.x, line.firstPoint.y)
                    lineTo(line.secondPoint.x, line.secondPoint.y)
                },
                width = strokeWidthPx,
                color = color,
                blendMode = DrawScope.DefaultBlendMode,
            )
        }
        return Frame(paths)
    }

    override fun generateFrames(
        amount: Int,
    ): List<Frame> {
        val frames = mutableListOf<Frame>()
        val startingTriangle = createInitTriangle()
        val lines = mutableListOf(*startingTriangle.lines.toTypedArray())
        val zoomPoint = startingTriangle.zoomPoint
        // Pre-zoom iterations
        var zoomInfo = ZoomInfo(zoomPoint, Float.MAX_VALUE)
        for (i in 0 until PRE_ZOOM_ITERATIONS_COUNT) {
            zoomInfo = lines.performKochTransformation(zoomPoint)
        }
        // Zoom with iterations
        var currentZoom = 1f
        for (i in 0 until amount) {
            lines.zoomTowards(zoomPoint, ZOOM_FACTOR_PER_FRAME)
            currentZoom *= ZOOM_FACTOR_PER_FRAME
            lines.removeOutOfBoundsLines()
            if (zoomInfo.shouldIterate()) {
                zoomInfo = lines.performKochTransformation(zoomPoint)
            }
            frames.add(lines.createFrame())
        }
        return frames
    }
}