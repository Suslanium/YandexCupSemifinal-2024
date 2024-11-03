package com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.extended.ExtendedColorSelector
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.theme.Primary
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBackground
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBorder

private val BottomContentHeight = 48.dp
private val SelectorRowPadding = 16.dp
private val SelectorRowContentElementSize = 32.dp
private val SelectorRowHeight = SelectorRowContentElementSize + SelectorRowPadding * 2
private val OffsetHeight = BottomContentHeight + SelectorRowHeight + 50.dp
private val SelectorRowWidth = SelectorRowContentElementSize * 5 + SelectorRowPadding * 6
private val ExpandedSelectorBottomOffset = BottomContentHeight + SelectorRowHeight + 8.dp

@Composable
fun BoxScope.ColorSelector(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    val offsetHeight = with(LocalDensity.current) {
        WindowInsets.systemBars.getBottom(density = this) + OffsetHeight.toPx()
    }
    val offsetY by animateFloatAsState(
        targetValue = if (!state.additionalToolsState.isColorSelectorVisible) offsetHeight else 0f,
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
                shape = RoundedCornerShape(4.dp),
            )
            .padding(SelectorRowPadding)
    ) {
        IconButton(
            onClick = { onEvent(MainScreenEvent.ExtendedColorSelectorClicked) },
            modifier = Modifier.size(SelectorRowContentElementSize),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_palette),
                contentDescription = null,
                tint = if (state.additionalToolsState.isColorSelectorExpanded) {
                    Primary
                } else {
                    Color.White
                },
            )
        }
        Spacer(modifier = Modifier.width(SelectorRowPadding))
        ColorSelectorItem(
            color = Color.White,
            onEvent = onEvent,
        )
        Spacer(modifier = Modifier.width(SelectorRowPadding))
        ColorSelectorItem(
            color = Color.Red,
            onEvent = onEvent,
        )
        Spacer(modifier = Modifier.width(SelectorRowPadding))
        ColorSelectorItem(
            color = Color.Black,
            onEvent = onEvent,
        )
        Spacer(modifier = Modifier.width(SelectorRowPadding))
        ColorSelectorItem(
            color = Color.Blue,
            onEvent = onEvent,
        )
    }
    AnimatedVisibility(
        state.additionalToolsState.isColorSelectorExpanded,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = ExpandedSelectorBottomOffset),
    ) {
        Column(
            modifier = Modifier
                .width(SelectorRowWidth)
                .clip(RoundedCornerShape(4.dp))
                .background(SemiTransparentBackground)
                .border(
                    width = 1.dp,
                    color = SemiTransparentBorder,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(SelectorRowPadding)
        ) {
            ExtendedColorSelector(
                initColor = state.selectedColor,
                onColorSet = { onEvent(MainScreenEvent.ColorSelected(it)) },
                onCancel = { onEvent(MainScreenEvent.ExtendedColorSelectorClicked) },
            )
        }
    }
}

@Composable
private fun ColorSelectorItem(
    color: Color,
    onEvent: (MainScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(SelectorRowContentElementSize)
            .clickable { onEvent(MainScreenEvent.ColorSelected(color)) }
            .background(
                color = color,
                shape = CircleShape,
            )
    )
}