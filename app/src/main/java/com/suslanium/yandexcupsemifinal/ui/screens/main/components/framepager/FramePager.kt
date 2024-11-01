package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
    val pagerState = rememberPagerState(initialPage = state.currentFrameIndex) {
        state.frames.size
    }
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(10),
    )
    val frameScale by animateFloatAsState(
        targetValue = if (state.interactionBlock == InteractionBlock.FrameSelect) 0.85f else 1f,
    )
    LaunchedEffect(state.currentFrameIndex) {
        if (state.currentFrameIndex == pagerState.currentPage) return@LaunchedEffect
        pagerState.scrollToPage(state.currentFrameIndex)
    }
    LaunchedEffect(state.interactionBlock == InteractionBlock.Playback) {
        if (state.interactionBlock == InteractionBlock.Playback) {
            ensureCurrentFrameIsShownAfterPlayback(pagerState, state)
            invokePlayback(pagerState, state)
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        flingBehavior = flingBehavior,
        userScrollEnabled = state.interactionBlock == InteractionBlock.FrameSelect,
    ) { frameIndex ->
        FrameDrawZone(
            state = state,
            onEvent = onEvent,
            frameIndex = frameIndex,
            modifier = Modifier.graphicsLayer {
                scaleX = frameScale
                scaleY = frameScale
            }
        )
    }
}