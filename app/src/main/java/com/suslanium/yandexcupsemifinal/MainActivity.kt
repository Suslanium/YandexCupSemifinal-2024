package com.suslanium.yandexcupsemifinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.ui.theme.YandexCupSemifinalTheme

//TODO move stuff to separate files
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexCupSemifinalTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    //TODO create a ViewModel and move state/paths there
    var state by remember {
        mutableStateOf(
            DrawScreenState(
                selectedColor = Color.Red,
                selectedWidth = 10.dp,
                interactionType = InteractionType.Drawing,
            )
        )
    }
    val paths = remember { mutableStateListOf<PathInfo>() }
    val currentPathPoints = remember { mutableStateListOf<Offset>() }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .systemBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DrawZone(
            currentPathPoints = currentPathPoints,
            paths = paths,
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp)),
        )
        Spacer(modifier = Modifier.height(16.dp))
        BottomToolbar(
            state = state,
            onEvent = { event ->
                //TODO handle events in ViewModel
                when (event) {
                    is DrawScreenEvent.InteractionTypeChanged -> {
                        state = state.copy(interactionType = event.interactionType)
                    }
                }
            },
        )
    }
}

@Composable
private fun BottomToolbar(
    state: DrawScreenState,
    onEvent: (DrawScreenEvent) -> Unit,
) {
    Row {
        IconButton(
            onClick = { onEvent(DrawScreenEvent.InteractionTypeChanged(InteractionType.Drawing)) },
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
            onClick = { onEvent(DrawScreenEvent.InteractionTypeChanged(InteractionType.Erasing)) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_eraser),
                contentDescription = null,
                tint = state.interactionType.iconColor(InteractionType.Erasing),
            )
        }
    }
}

@Composable
private fun InteractionType.iconColor(iconType: InteractionType) =
    if (this == iconType) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

@Composable
private fun DrawZone(
    currentPathPoints: SnapshotStateList<Offset>,
    paths: SnapshotStateList<PathInfo>,
    state: DrawScreenState,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .paint(
                painter = painterResource(R.drawable.drawing_bg),
                contentScale = ContentScale.Crop,
            )
            .pointerInput(state) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPathPoints.add(offset)
                    },
                    onDragEnd = {
                        val pathInfo = createPathInfo(
                            currentPathPoints = currentPathPoints,
                            state = state,
                        )
                        paths.add(pathInfo)
                        currentPathPoints.clear()
                    },
                    onDragCancel = {
                        val pathInfo = createPathInfo(
                            currentPathPoints = currentPathPoints,
                            state = state,
                        )
                        paths.add(pathInfo)
                        currentPathPoints.clear()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentPathPoints.add(change.position)
                    },
                )
            }
    ) {
        with(drawContext.canvas.nativeCanvas) {
            val checkPoint = saveLayer(null, null)
            paths.forEach { pathInfo ->
                drawPath(
                    color = pathInfo.color,
                    path = pathInfo.path,
                    style = Stroke(
                        width = pathInfo.width,
                        cap = StrokeCap.Round,
                    ),
                    blendMode = pathInfo.blendMode,
                )
            }
            drawPath(
                color = state.actualColor,
                path = createPathFromPoints(currentPathPoints),
                style = Stroke(
                    width = state.selectedWidth.toPx(),
                    cap = StrokeCap.Round,
                ),
                blendMode = state.blendMode,
            )
            restoreToCount(checkPoint)
        }
    }
}

private fun createPathFromPoints(currentPathPoints: Iterable<Offset>): Path {
    return Path().apply {
        var prevPoint: Offset? = null
        currentPathPoints.forEach { point ->
            prevPoint?.let { prevPoint ->
                quadraticBezierTo(
                    prevPoint.x,
                    prevPoint.y,
                    (prevPoint.x + point.x) / 2,
                    (prevPoint.y + point.y) / 2
                )
            } ?: moveTo(point.x, point.y)
            prevPoint = point
        }
    }
}

enum class InteractionType {
    Drawing,
    Erasing,
}

data class DrawScreenState(
    val selectedColor: Color,
    val selectedWidth: Dp,
    val interactionType: InteractionType,
) {
    val actualColor: Color
        get() = when (interactionType) {
            InteractionType.Drawing -> selectedColor
            InteractionType.Erasing -> Color.Transparent
        }
    val blendMode: BlendMode
        get() = when (interactionType) {
            InteractionType.Drawing -> DefaultBlendMode
            InteractionType.Erasing -> BlendMode.Clear
        }
}

data class PathInfo(
    val path: Path,
    val width: Float,
    val color: Color,
    val blendMode: BlendMode,
)

private fun Density.createPathInfo(
    currentPathPoints: Iterable<Offset>,
    state: DrawScreenState,
): PathInfo {
    return PathInfo(
        path = createPathFromPoints(currentPathPoints),
        width = state.selectedWidth.toPx(),
        color = state.actualColor,
        blendMode = state.blendMode,
    )
}

sealed interface DrawScreenEvent {

    data class InteractionTypeChanged(val interactionType: InteractionType) : DrawScreenEvent

}