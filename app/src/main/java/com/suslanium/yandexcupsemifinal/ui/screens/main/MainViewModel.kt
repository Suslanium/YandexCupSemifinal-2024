package com.suslanium.yandexcupsemifinal.ui.screens.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.Frame
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionType
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.createPathInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    defaultLineWidthPx: Float,
    defaultColor: Color,
) : ViewModel() {

    //TODO implement non integer-constrained list
    private val frames = mutableStateListOf(Frame())
    private val newPathPoints = mutableStateListOf<Offset>()

    private val _state = MutableStateFlow(
        MainScreenState(
            selectedColor = defaultColor,
            selectedWidthPx = defaultLineWidthPx,
            interactionType = InteractionType.Drawing,
            interactionBlock = InteractionBlock.None,
            additionalToolsState = AdditionalToolsState.Hidden,
            newPathPoints = newPathPoints,
            frames = frames,
            currentFrameIndex = 0,
        )
    )
    val state = _state.asStateFlow()

    fun processEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.InteractionTypeChanged -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                _state.update { it.copy(interactionType = event.interactionType) }
            }

            is MainScreenEvent.NewPathStarted -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                newPathPoints.add(event.startPoint)
            }

            MainScreenEvent.PathFinished -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val pathInfo = createPathInfo(
                    state = _state.value,
                )
                val frame = frames[_state.value.currentFrameIndex]
                frame.mutablePaths.add(pathInfo)
                frame.mutableRedoStack.clear()
                newPathPoints.clear()
            }

            is MainScreenEvent.PathPointAdded -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                newPathPoints.add(event.point)
            }

            MainScreenEvent.Redo -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val frame = frames[_state.value.currentFrameIndex]
                frame.mutableRedoStack.removeLastOrNull()?.let { pathInfo ->
                    frame.mutablePaths.add(pathInfo)
                }
            }

            MainScreenEvent.Undo -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val frame = frames[_state.value.currentFrameIndex]
                frame.mutablePaths.removeLastOrNull()?.let { pathInfo ->
                    frame.mutableRedoStack.add(pathInfo)
                }
            }

            is MainScreenEvent.ColorSelected -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState == AdditionalToolsState.Hidden) return
                _state.update {
                    it.copy(
                        selectedColor = event.color,
                        additionalToolsState = AdditionalToolsState.Hidden,
                        interactionType = InteractionType.Drawing,
                    )
                }
            }

            MainScreenEvent.ColorSelectorClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                _state.update {
                    it.copy(
                        additionalToolsState = if (!it.additionalToolsState.isColorSelectorVisible) {
                            AdditionalToolsState.ColorSelector(isExpanded = false)
                        } else {
                            AdditionalToolsState.Hidden
                        }
                    )
                }
            }

            MainScreenEvent.ExtendedColorSelectorClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val additionalToolsState =
                    _state.value.additionalToolsState as? AdditionalToolsState.ColorSelector ?: return
                _state.update {
                    it.copy(
                        additionalToolsState = additionalToolsState.copy(
                            isExpanded = !additionalToolsState.isExpanded,
                        )
                    )
                }
            }

            MainScreenEvent.WidthSelectorClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                _state.update {
                    it.copy(
                        additionalToolsState = if (it.additionalToolsState == AdditionalToolsState.WidthSelector) {
                            AdditionalToolsState.Hidden
                        } else {
                            AdditionalToolsState.WidthSelector
                        }
                    )
                }
            }

            is MainScreenEvent.WidthSelected -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                _state.update {
                    it.copy(selectedWidthPx = event.widthPx)
                }
            }

            MainScreenEvent.DeleteFrameClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (!_state.value.isFrameDeletionAvailable) return
                if (newPathPoints.isNotEmpty()) {
                    //TODO cancel interaction
                    newPathPoints.clear()
                }
                _state.value = _state.value.copy(
                    currentFrameIndex = _state.value.currentFrameIndex - 1,
                )
                frames.removeAt(_state.value.currentFrameIndex + 1)
            }

            MainScreenEvent.NewFrameClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (newPathPoints.isNotEmpty()) {
                    //TODO cancel interaction
                    processEvent(MainScreenEvent.PathFinished)
                }
                frames.add(_state.value.currentFrameIndex + 1, Frame())
                _state.update {
                    it.copy(currentFrameIndex = it.currentFrameIndex + 1)
                }
            }

            MainScreenEvent.StartPlayback -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (!_state.value.isPlaybackAvailable) return
                if (newPathPoints.isNotEmpty()) {
                    newPathPoints.clear()
                }
                _state.update {
                    it.copy(
                        interactionBlock = InteractionBlock.Playback,
                        additionalToolsState = AdditionalToolsState.Hidden,
                    )
                }
            }

            MainScreenEvent.StopPlayback -> {
                if (!_state.value.isPlaybackPauseAvailable) return
                _state.update {
                    it.copy(
                        interactionBlock = InteractionBlock.None,
                    )
                }
            }

            is MainScreenEvent.FrameSelected -> {
                if (_state.value.interactionBlock != InteractionBlock.FrameSelect) return
                _state.update {
                    it.copy(
                        currentFrameIndex = event.frameIndex,
                        interactionBlock = InteractionBlock.None,
                    )
                }
            }

            MainScreenEvent.FrameSelectionClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (newPathPoints.isNotEmpty()) {
                    newPathPoints.clear()
                }
                _state.update {
                    it.copy(
                        interactionBlock = InteractionBlock.FrameSelect,
                        additionalToolsState = AdditionalToolsState.Hidden,
                    )
                }
            }
        }
    }

}

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val defaultLineWidthPx: Float,
    private val defaultColor: Color,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(defaultLineWidthPx, defaultColor) as T
    }
}