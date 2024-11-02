package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FramePager(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val frameScale by animateFloatAsState(
        targetValue = if (state.interactionBlock == InteractionBlock.FrameSelect) 0.85f else 1f,
    )
    val flingBehaviorSpec = flingBehaviorSpec(
        pagerSnapDistance = PagerSnapDistance.atMost(10),
    )

    LongHorizontalPager(
        pageCount = state.frames.size,
        userScrollEnabled = state.interactionBlock == InteractionBlock.FrameSelect,
        modifier = modifier,
        flingBehaviorSpec = flingBehaviorSpec,
        scopeActions = {
            LaunchedEffect(state.currentFrameIndex) {
                if (state.currentFrameIndex == currentPage) return@LaunchedEffect
                scrollToPage(state.currentFrameIndex)
            }

            LaunchedEffect(state.interactionBlock == InteractionBlock.Playback) {
                if (state.interactionBlock == InteractionBlock.Playback) {
                    ensureCurrentFrameIsShownAfterPlayback(this@LongHorizontalPager, state)
                    invokePlayback(state)
                }
            }
        },
    ) { frameIndex ->
        FrameDrawZone(
            state = state,
            onEvent = onEvent,
            frameIndex = frameIndex,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    scaleX = frameScale
                    scaleY = frameScale
                }
                .clip(RoundedCornerShape(20.dp))
                .paint(
                    painter = painterResource(R.drawable.drawing_bg),
                    contentScale = ContentScale.Crop,
                ),
        )
    }
}