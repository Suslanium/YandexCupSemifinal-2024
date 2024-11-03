package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.createPathFromPoints

@Composable
fun FrameDrawZone(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    frameIndex: Long,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .pointerInput(state) {
                if (state.interactionBlock == InteractionBlock.FrameSelect) {
                    detectTapGestures {
                        onEvent(MainScreenEvent.FrameSelected(frameIndex))
                    }
                } else if (state.interactionBlock == InteractionBlock.None) {
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
            }.onSizeChanged { size ->
                onEvent(
                    MainScreenEvent.DrawCanvasSizeChanged(
                        width = size.width,
                        height = size.height
                    )
                )
            }
    ) {
        with(drawContext.canvas.nativeCanvas) {
            if (frameIndex > 0 && state.interactionBlock == InteractionBlock.None) {
                val firstCheckPoint = saveLayer(null, null)
                state.frames[frameIndex - 1].paths.forEach { pathInfo ->
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
            state.frames[frameIndex].paths.forEach { pathInfo ->
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
                path = createPathFromPoints(state.newPathPoints),
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