package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.createPathFromPoints

@Composable
fun DrawZone(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .paint(
                painter = painterResource(R.drawable.drawing_bg),
                contentScale = ContentScale.Crop,
            )
            .pointerInput(state) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onEvent(MainScreenEvent.NewPathStarted(offset))
                    },
                    onDragEnd = {
                        onEvent(MainScreenEvent.PathFinished)
                    },
                    onDragCancel = {
                        onEvent(MainScreenEvent.PathFinished)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onEvent(MainScreenEvent.PathPointAdded(change.position))
                    },
                )
            }
    ) {
        with(drawContext.canvas.nativeCanvas) {
            if (state.currentFrameIndex > 0) {
                val firstCheckPoint = saveLayer(null, null)
                state.frames[state.currentFrameIndex - 1].paths.forEach { pathInfo ->
                    drawPath(
                        color = pathInfo.color.copy(alpha = 0.5f),
                        path = pathInfo.path,
                        style = Stroke(
                            width = pathInfo.width,
                            cap = StrokeCap.Round,
                        ),
                        blendMode = pathInfo.blendMode,
                    )
                }
                restoreToCount(firstCheckPoint)
            }
            val secondCheckPoint = saveLayer(null, null)
            state.frames[state.currentFrameIndex].paths.forEach { pathInfo ->
                drawPath(
                    color = pathInfo.color,
                    path = pathInfo.path,
                    style = Stroke(
                        width = pathInfo.width,
                        cap = StrokeCap.Round,
                    ),
                    blendMode = pathInfo.blendMode,
                )
            }
            drawPath(
                color = state.actualColor,
                path = createPathFromPoints(state.currentPathPoints),
                style = Stroke(
                    width = state.selectedWidthPx,
                    cap = StrokeCap.Round,
                ),
                blendMode = state.blendMode,
            )
            restoreToCount(secondCheckPoint)
        }
    }
}