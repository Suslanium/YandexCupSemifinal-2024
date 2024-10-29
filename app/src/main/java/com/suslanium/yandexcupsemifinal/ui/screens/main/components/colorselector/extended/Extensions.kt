package com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.extended

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.draggableClickable(
    onDrag: (horizontalDelta: Float) -> Unit,
    onClick: (horizontalOffset: Float) -> Unit,
): Modifier {
    return this
        .draggable(
            orientation = Orientation.Horizontal,
            state = rememberDraggableState(onDelta = onDrag),
        )
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                onClick(offset.x)
            }
        }
}