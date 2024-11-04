package com.suslanium.yandexcupsemifinal.ui.screens.main.components.popup

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBackground
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBorder

private val TopContentHeight = 64.dp
private val TopPopupMenuPadding = 16.dp
private val TopPopupMenuItemSize = 32.dp
private val TopPopupMenuHeight =
    TopPopupMenuItemSize * (topPopupMenuItems.size) + TopPopupMenuPadding * (topPopupMenuItems.size + 1)
private val TopPopupMenuWidth = TopPopupMenuItemSize * 5 + TopPopupMenuPadding * 4
private val OffsetHeight = TopContentHeight + TopPopupMenuHeight + 50.dp

@Composable
fun BoxScope.TopPopupMenu(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
) {
    val density = LocalDensity.current
    val offsetHeight = with(density) {
        WindowInsets.systemBars.getTop(density = this) + OffsetHeight.toPx()
    }
    val offsetY by animateFloatAsState(
        targetValue = if (state.additionalToolsState != AdditionalToolsState.TopPopupMenu) -offsetHeight else 0f,
    )

    Column(
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
                shape = RoundedCornerShape(4.dp),
            )
            .padding(TopPopupMenuPadding)
            .defaultMinSize(
                minWidth = TopPopupMenuWidth,
            )
    ) {
        topPopupMenuItems.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.clickable { onEvent(item.onClickEvent) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(item.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(TopPopupMenuItemSize),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(item.titleRes),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (index != topPopupMenuItems.lastIndex) {
                Spacer(modifier = Modifier.height(TopPopupMenuPadding))
            }
        }
    }
}