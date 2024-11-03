package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBackground
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBorder

private val BottomContentHeight = 48.dp
private val SelectorRowPadding = 16.dp
private val SelectorRowContentElementSize = 32.dp
private val SelectorRowHeight = SelectorRowContentElementSize + SelectorRowPadding * 2
private val OffsetHeight = BottomContentHeight + SelectorRowHeight + 50.dp
private val SelectorRowWidth = SelectorRowContentElementSize * 5 + SelectorRowPadding * 4

@Composable
fun BoxScope.WidthSelector(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    val density = LocalDensity.current
    var isDraggingSlider by remember { mutableStateOf(false) }
    val offsetHeight = with(LocalDensity.current) {
        WindowInsets.systemBars.getBottom(density = this) + OffsetHeight.toPx()
    }
    val offsetY by animateFloatAsState(
        targetValue = if (state.additionalToolsState != AdditionalToolsState.WidthSelector) offsetHeight else 0f,
    )

    Row(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY
            }
            .align(Alignment.BottomCenter)
            .padding(bottom = BottomContentHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(SemiTransparentBackground)
            .border(
                width = 1.dp,
                color = SemiTransparentBorder,
                shape = RoundedCornerShape(4.dp))
            .padding(SelectorRowPadding)
    ) {
        Slider(
            value = with(density) { state.selectedWidthPx.toDp().value },
            onValueChange = { value ->
                isDraggingSlider = true
                onEvent(
                    MainScreenEvent.WidthSelected(
                        widthPx = with(density) { value.dp.toPx() }
                    )
                )
            },
            modifier = Modifier
                .width(SelectorRowWidth)
                .height(SelectorRowContentElementSize),
            onValueChangeFinished = {
                isDraggingSlider = false
            },
            steps = 30,
            valueRange = 1f..30f,
        )
    }

    if (isDraggingSlider) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(with (density) { state.selectedWidthPx.toDp() })
                .background(
                    color = state.selectedColor,
                    shape = CircleShape,
                )
        )
    }
}