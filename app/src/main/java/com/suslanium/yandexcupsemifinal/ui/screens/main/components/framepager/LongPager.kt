package com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerDefaults.LowVelocityAnimationSpec
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

private const val PAGER_PAGE_COUNT = 1001L

interface LongPagerScope {
    suspend fun scrollToPage(page: Long)

    val currentPage: Long
}

class LoopIndexWithPagerPage(
    val loopIndex: Long,
    val page: Int,
)

private fun getLoopIndexWithPagerPage(actualPage: Long): LoopIndexWithPagerPage {
    val offset = actualPage / (PAGER_PAGE_COUNT - 2)
    val newPage = actualPage % (PAGER_PAGE_COUNT - 2)
    return if (offset > 0 && newPage == 0L) {
        LoopIndexWithPagerPage(offset - 1, (PAGER_PAGE_COUNT - 2).toInt())
    } else {
        LoopIndexWithPagerPage(offset, newPage.toInt())
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun calculateActualPageIndex(pagerState: PagerState, loopCount: Long): Long {
    return calculateActualPageIndex(pagerState.currentPage, loopCount)
}

private fun calculateActualPageIndex(pagerPage: Int, loopCount: Long): Long {
    return loopCount * (PAGER_PAGE_COUNT - 2) + pagerPage
}

class FlingBehaviorSpec @OptIn(ExperimentalFoundationApi::class) constructor(
    val pagerSnapDistance: PagerSnapDistance,
    val lowVelocityAnimationSpec: AnimationSpec<Float>,
    val highVelocityAnimationSpec: DecayAnimationSpec<Float>,
    val snapAnimationSpec: AnimationSpec<Float>,
    @FloatRange(from = 0.0, to = 1.0) val snapPositionalThreshold: Float,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun flingBehaviorSpec(
    pagerSnapDistance: PagerSnapDistance = PagerSnapDistance.atMost(1),
    lowVelocityAnimationSpec: AnimationSpec<Float> = LowVelocityAnimationSpec,
    highVelocityAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    snapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    @FloatRange(from = 0.0, to = 1.0) snapPositionalThreshold: Float = 0.5f,
): FlingBehaviorSpec {
    return FlingBehaviorSpec(
        pagerSnapDistance = pagerSnapDistance,
        lowVelocityAnimationSpec = lowVelocityAnimationSpec,
        highVelocityAnimationSpec = highVelocityAnimationSpec,
        snapAnimationSpec = snapAnimationSpec,
        snapPositionalThreshold = snapPositionalThreshold,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LongHorizontalPager(
    pageCount: Long,
    userScrollEnabled: Boolean,
    modifier: Modifier = Modifier,
    flingBehaviorSpec: FlingBehaviorSpec = flingBehaviorSpec(),
    scopeActions: @Composable LongPagerScope.() -> Unit = {},
    content: @Composable (Long) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        minOf(pageCount, PAGER_PAGE_COUNT).toInt()
    }
    val flingBehavior = PagerDefaults.flingBehavior(
        state = pagerState,
        pagerSnapDistance = flingBehaviorSpec.pagerSnapDistance,
        lowVelocityAnimationSpec = flingBehaviorSpec.lowVelocityAnimationSpec,
        highVelocityAnimationSpec = flingBehaviorSpec.highVelocityAnimationSpec,
        snapAnimationSpec = flingBehaviorSpec.snapAnimationSpec,
        snapPositionalThreshold = flingBehaviorSpec.snapPositionalThreshold,
    )

    var currentLoopIndex by rememberSaveable { mutableLongStateOf(0) }
    val scope = object : LongPagerScope {
        override suspend fun scrollToPage(page: Long) {
            val loopIndexWithPagerPage = getLoopIndexWithPagerPage(page)
            if (currentLoopIndex != loopIndexWithPagerPage.loopIndex) {
                currentLoopIndex = loopIndexWithPagerPage.loopIndex
            }
            pagerState.scrollToPage(loopIndexWithPagerPage.page)
        }

        override val currentPage: Long
            get() = calculateActualPageIndex(pagerState, currentLoopIndex)
    }
    scope.scopeActions()

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        val actualPage = calculateActualPageIndex(pagerState, currentLoopIndex)
        if (actualPage >= pageCount) {
            val loopIndexWithPagerPage = getLoopIndexWithPagerPage(pageCount - 1)
            currentLoopIndex = loopIndexWithPagerPage.loopIndex
            withContext(NonCancellable) {
                pagerState.animateScrollToPage(loopIndexWithPagerPage.page)
            }
        } else if (!pagerState.isScrollInProgress) {
            if (pagerState.currentPage.toLong() == PAGER_PAGE_COUNT - 1 && actualPage < pageCount - 1) {
                currentLoopIndex++
                pagerState.scrollToPage(1)
            } else if (pagerState.currentPage == 0 && actualPage > 0) {
                currentLoopIndex--
                pagerState.scrollToPage((PAGER_PAGE_COUNT - 2).toInt())
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) { page ->
        val actualPage = calculateActualPageIndex(page, currentLoopIndex)
        if (actualPage >= pageCount) return@HorizontalPager
        content(actualPage)
    }
}