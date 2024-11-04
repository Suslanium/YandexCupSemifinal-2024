package com.suslanium.yandexcupsemifinal.ui.screens.main

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.Frame
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionType
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEffect
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.generator.createFrameGenerator
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.list.mutableLongListOf
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.frame.gif.saveGif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    defaultLineWidthPx: Float,
    defaultColor: Color,
    private val applicationContext: Context,
) : ViewModel() {

    companion object {
        const val GIF_MIME_TYPE = "image/gif"
        const val ANIMATION_FILE_NAME = "animation.gif"
    }

    private val frames = mutableLongListOf(Frame())
    private val newPathPoints = mutableStateListOf<Offset>()
    private var canvasWidth = 0
    private var canvasHeight = 0

    private val _state = MutableStateFlow(
        MainScreenState(
            selectedColor = defaultColor,
            selectedWidthPx = defaultLineWidthPx,
            selectedPlaybackFps = 24,
            interactionType = InteractionType.Drawing,
            interactionBlock = InteractionBlock.None,
            additionalToolsState = AdditionalToolsState.Hidden,
            newPathPoints = newPathPoints,
            frames = frames,
            currentFrameIndex = 0,
        )
    )
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<MainScreenEffect>()
    val effects = _effects.asSharedFlow()

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
                val frame = frames[_state.value.currentFrameIndex]
                frame.addPath(_state.value)
                newPathPoints.clear()
            }

            is MainScreenEvent.PathPointAdded -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                newPathPoints.add(event.point)
            }

            MainScreenEvent.Redo -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val frame = frames[_state.value.currentFrameIndex]
                frame.redo()
            }

            MainScreenEvent.Undo -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                val frame = frames[_state.value.currentFrameIndex]
                frame.undo()
            }

            is MainScreenEvent.ColorSelected -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState !is AdditionalToolsState.ColorSelector) return
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
                    processEvent(MainScreenEvent.PathFinished)
                }
                frames.add(_state.value.currentFrameIndex + 1, Frame())
                _state.update {
                    it.copy(currentFrameIndex = it.currentFrameIndex + 1)
                }
            }

            MainScreenEvent.StartStopPlayback -> {
                if (_state.value.interactionBlock == InteractionBlock.None) {
                    if (frames.size <= 1) return
                    if (newPathPoints.isNotEmpty()) {
                        newPathPoints.clear()
                    }
                    _state.update {
                        it.copy(
                            interactionBlock = InteractionBlock.Playback,
                            additionalToolsState = AdditionalToolsState.Hidden,
                        )
                    }
                } else if (_state.value.interactionBlock == InteractionBlock.Playback) {
                    _state.update {
                        it.copy(
                            interactionBlock = InteractionBlock.None,
                        )
                    }
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

            MainScreenEvent.TopPopupMenuClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                _state.update {
                    it.copy(
                        additionalToolsState =
                        if (it.additionalToolsState == AdditionalToolsState.TopPopupMenu ||
                            it.additionalToolsState == AdditionalToolsState.SpeedSelector) {
                            AdditionalToolsState.Hidden
                        } else {
                            AdditionalToolsState.TopPopupMenu
                        }
                    )
                }
            }

            MainScreenEvent.DeleteAllFramesClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.TopPopupMenu) return
                frames.clear()
                frames.add(0, Frame())
                _state.update {
                    it.copy(
                        currentFrameIndex = 0,
                        additionalToolsState = AdditionalToolsState.Hidden,
                    )
                }
            }

            MainScreenEvent.DuplicateFrameClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.TopPopupMenu) return
                if (newPathPoints.isNotEmpty()) {
                    newPathPoints.clear()
                }
                frames.add(_state.value.currentFrameIndex + 1, frames[_state.value.currentFrameIndex].copy())
                _state.update {
                    it.copy(
                        currentFrameIndex = it.currentFrameIndex + 1,
                        additionalToolsState = AdditionalToolsState.Hidden,
                    )
                }
            }

            MainScreenEvent.SpeedSelectorClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.TopPopupMenu) return
                _state.update {
                    it.copy(
                        additionalToolsState = AdditionalToolsState.SpeedSelector,
                    )
                }
            }

            is MainScreenEvent.SpeedSelected -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.SpeedSelector) return
                _state.update {
                    it.copy(
                        selectedPlaybackFps = event.fps.coerceIn(1, 60),
                    )
                }
            }

            is MainScreenEvent.DrawCanvasSizeChanged -> {
                canvasWidth = event.width
                canvasHeight = event.height
            }

            MainScreenEvent.ExportToGifClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.TopPopupMenu) return
                _state.update {
                    it.copy(
                        additionalToolsState = AdditionalToolsState.Hidden,
                    )
                }
                viewModelScope.launch {
                    _effects.emit(MainScreenEffect.ChooseFileLocation)
                }
            }

            is MainScreenEvent.ExportToGif -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                viewModelScope.launch(Dispatchers.Default) {
                    _state.update {
                        it.copy(
                            interactionBlock = InteractionBlock.GifSaving,
                        )
                    }
                    runCatching {
                        applicationContext.saveGif(
                            uri = event.uri,
                            frames = frames,
                            canvasWidth = canvasWidth,
                            canvasHeight = canvasHeight,
                            frameRate = _state.value.selectedPlaybackFps,
                        )
                    }.onFailure {
                        viewModelScope.launch { _effects.emit(MainScreenEffect.ShowGifExportError) }
                    }
                    _state.update {
                        it.copy(
                            interactionBlock = InteractionBlock.None,
                        )
                    }
                }
            }

            MainScreenEvent.FrameGenerationClicked -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.TopPopupMenu) return
                _state.update {
                    it.copy(
                        additionalToolsState = AdditionalToolsState.FrameGenerationDialog,
                    )
                }
            }

            is MainScreenEvent.FrameGenerationConfirmed -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.FrameGenerationDialog) return
                if (event.frameAmount.isBlank()) {
                    viewModelScope.launch { _effects.emit(MainScreenEffect.ShowEmptyFrameAmountError) }
                    return
                }
                runCatching {
                    event.frameAmount.toInt()
                }.onFailure {
                    viewModelScope.launch { _effects.emit(MainScreenEffect.ShowInvalidFrameAmountError) }
                    return
                }.onSuccess { frameAmount ->
                    if (frameAmount < 1) {
                        viewModelScope.launch { _effects.emit(MainScreenEffect.ShowInvalidFrameAmountError) }
                        return
                    }
                    _state.update {
                        it.copy(
                            additionalToolsState = AdditionalToolsState.Hidden,
                            interactionBlock = InteractionBlock.FrameGeneration,
                        )
                    }
                    viewModelScope.launch(Dispatchers.Default) {
                        createFrameGenerator(
                            type = event.type,
                            canvasWidth = canvasWidth,
                            canvasHeight = canvasHeight,
                            color = _state.value.selectedColor,
                            strokeWidthPx = _state.value.selectedWidthPx,
                        ).generateFrames(frameAmount).forEachIndexed { index, frame ->
                            frames.add(_state.value.currentFrameIndex + index + 1, frame)
                        }
                        _state.update {
                            it.copy(
                                currentFrameIndex = it.currentFrameIndex + 1,
                                interactionBlock = InteractionBlock.None,
                            )
                        }
                    }
                }
            }

            MainScreenEvent.FrameGenerationDismissed -> {
                if (_state.value.interactionBlock != InteractionBlock.None) return
                if (_state.value.additionalToolsState != AdditionalToolsState.FrameGenerationDialog) return
                _state.update {
                    it.copy(
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
    private val applicationContext: Context,
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(defaultLineWidthPx, defaultColor, applicationContext) as T
    }
}