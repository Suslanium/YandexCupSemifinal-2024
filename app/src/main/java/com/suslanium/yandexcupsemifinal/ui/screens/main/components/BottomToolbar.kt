package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionType
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.iconColor

@Composable
fun BottomToolbar(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(
        targetValue = if (state.interactionBlock == InteractionBlock.None) 1f else 0f,
    )
    Row(modifier = modifier.alpha(alpha), horizontalArrangement = Arrangement.Center) {
        IconButton(
            onClick = { onEvent(MainScreenEvent.InteractionTypeChanged(InteractionType.Drawing)) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_pencil),
                contentDescription = null,
                tint = state.interactionType.iconColor(InteractionType.Drawing),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onEvent(MainScreenEvent.InteractionTypeChanged(InteractionType.Erasing)) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_eraser),
                contentDescription = null,
                tint = state.interactionType.iconColor(InteractionType.Erasing),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onEvent(MainScreenEvent.WidthSelectorClicked) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_menu),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (state.additionalToolsState == AdditionalToolsState.WidthSelector) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onEvent(MainScreenEvent.ColorSelectorClicked) },
            modifier = Modifier.size(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = state.selectedColor,
                        shape = CircleShape,
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = if (state.additionalToolsState.isColorSelectorVisible) 1f else 0f,
                        ),
                        shape = CircleShape,
                    )
            )
        }
    }
}