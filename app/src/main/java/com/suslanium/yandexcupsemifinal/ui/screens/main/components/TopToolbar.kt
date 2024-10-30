package com.suslanium.yandexcupsemifinal.ui.screens.main.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState

@Composable
fun TopToolbar(
    state: MainScreenState,
    onEvent: (MainScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onEvent(MainScreenEvent.Undo) },
            modifier = Modifier.size(24.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (state.isUndoAvailable) 1f else 0.5f,
                ),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = { onEvent(MainScreenEvent.Redo) },
            modifier = Modifier.size(24.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_redo),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (state.isRedoAvailable) 1f else 0.5f,
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onEvent(MainScreenEvent.DeleteFrameClicked) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_delete_frame),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (state.isFrameDeletionAvailable) 1f else 0.5f,
                ),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = { onEvent(MainScreenEvent.NewFrameClicked) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_new_frame),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}