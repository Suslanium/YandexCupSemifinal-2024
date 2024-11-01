package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalFoundationApi::class)
fun CoroutineScope.ensureCurrentFrameIsShownAfterPlayback(
    pagerState: PagerState,
    state: MainScreenState,
) {
    launch {
        runCatching {
            awaitCancellation()
        }.onFailure {
            withContext(NonCancellable) {
                pagerState.scrollToPage(state.currentFrameIndex)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun invokePlayback(
    pagerState: PagerState,
    state: MainScreenState,
) {
    while (true) {
        if (pagerState.currentPage == state.frames.lastIndex) {
            pagerState.scrollToPage(0)
            delay(32)
        }
        pagerState.scrollToPage(pagerState.currentPage + 1)
        delay(32)
    }
}