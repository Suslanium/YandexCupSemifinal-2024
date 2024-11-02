package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun CoroutineScope.ensureCurrentFrameIsShownAfterPlayback(
    longPagerScope: LongPagerScope,
    state: MainScreenState,
) {
    launch {
        runCatching {
            awaitCancellation()
        }.onFailure {
            withContext(NonCancellable) {
                longPagerScope.scrollToPage(state.currentFrameIndex)
            }
        }
    }
}

suspend fun LongPagerScope.invokePlayback(
    state: MainScreenState,
) {
    while (true) {
        if (currentPage == state.frames.lastIndex) {
            scrollToPage(0)
            delay(32)
        }
        scrollToPage(currentPage + 1)
        delay(32)
    }
}