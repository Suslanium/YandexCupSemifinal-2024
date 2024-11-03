package com.suslanium.yandexcupsemifinal.ui.screens.main.components

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
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBackground
import com.suslanium.yandexcupsemifinal.ui.theme.SemiTransparentBorder

private val TopContentHeight = 64.dp
private val TopPopupMenuPadding = 16.dp
private val TopPopupMenuItemSize = 32.dp
private val TopPopupMenuHeight = TopPopupMenuItemSize * 4 + TopPopupMenuPadding * 5
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
        Row(
            modifier = Modifier.clickable { onEvent(MainScreenEvent.DeleteAllFramesClicked) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_delete_frame),
                contentDescription = null,
                modifier = Modifier.size(TopPopupMenuItemSize),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.delete_all_frames),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(TopPopupMenuPadding))
        Row(
            modifier = Modifier.clickable { onEvent(MainScreenEvent.DuplicateFrameClicked) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                contentDescription = null,
                modifier = Modifier.size(TopPopupMenuItemSize),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.duplicate_frame),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(TopPopupMenuPadding))
        Row(
            modifier = Modifier.clickable { onEvent(MainScreenEvent.SpeedSelectorClicked) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_speed),
                contentDescription = null,
                modifier = Modifier.size(TopPopupMenuItemSize),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.change_speed),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.height(TopPopupMenuPadding))
        Row(
            modifier = Modifier.clickable { onEvent(MainScreenEvent.ExportToGifClicked) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_gif),
                contentDescription = null,
                modifier = Modifier.size(TopPopupMenuItemSize),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(R.string.export_gif),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}