package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

private val TopContentHeight = 64.dp
private val SpeedSelectorPadding = 16.dp
private val SpeedSelectorContentElementSize = 32.dp
private val SpeedSelectorHeight = SpeedSelectorContentElementSize + SpeedSelectorPadding * 2
private val OffsetHeight = TopContentHeight + SpeedSelectorHeight + 50.dp
private val SpeedSelectorWidth = SpeedSelectorContentElementSize * 5 + SpeedSelectorPadding * 4

@Composable
fun BoxScope.SpeedSelector(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    val density = LocalDensity.current
    val offsetHeight = with(density) {
        WindowInsets.systemBars.getTop(density = this) + OffsetHeight.toPx()
    }
    val offsetY by animateFloatAsState(
        targetValue = if (state.additionalToolsState != AdditionalToolsState.SpeedSelector) -offsetHeight else 0f,
    )

    Row(
        modifier = Modifier
            .graphicsLayer {
                translationY = offsetY
            }
            .align(Alignment.TopCenter)
            .padding(top = TopContentHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(SemiTransparentBackground)
            .border(
                width = 1.dp,
                color = SemiTransparentBorder,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(SpeedSelectorPadding)
    ) {
        Slider(
            value = state.selectedPlaybackFps.toFloat(),
            onValueChange = { value ->
                onEvent(MainScreenEvent.SpeedSelected(value.toInt()))
            },
            modifier = Modifier
                .width(SpeedSelectorWidth)
                .height(SpeedSelectorContentElementSize),
            steps = 60,
            valueRange = 1f..60f,
        )
    }
}