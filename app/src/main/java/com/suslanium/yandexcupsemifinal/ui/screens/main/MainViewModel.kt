package com.suslanium.yandexcupsemifinal.ui.screens.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.AdditionalToolsState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenState
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionType
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.PathInfo
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.createPathInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    defaultLineWidthPx: Float,
    defaultColor: Color,
) : ViewModel() {

    private val paths = mutableStateListOf<PathInfo>()
    private val currentPathPoints = mutableStateListOf<Offset>()
    private val redoStack = mutableStateListOf<PathInfo>()

    private val _state = MutableStateFlow(
        MainScreenState(
            selectedColor = defaultColor,
            selectedWidthPx = defaultLineWidthPx,
            interactionType = InteractionType.Drawing,
            additionalToolsState = AdditionalToolsState.Hidden,
            isRedoAvailableProvider = { redoStack.isNotEmpty() },
            isUndoAvailableProvider = { paths.isNotEmpty() },
            pathsProvider = { paths },
            currentPathPointsProvider = { currentPathPoints },
        )
    )
    val state = _state.asStateFlow()

    fun processEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.InteractionTypeChanged -> {
                _state.update { it.copy(interactionType = event.interactionType) }
            }

            is MainScreenEvent.NewPathStarted -> {
                currentPathPoints.add(event.startPoint)
            }

            MainScreenEvent.PathFinished -> {
                val pathInfo = createPathInfo(
                    currentPathPoints = currentPathPoints,
                    state = _state.value,
                )
                paths.add(pathInfo)
                redoStack.clear()
                currentPathPoints.clear()
            }

            is MainScreenEvent.PathPointAdded -> {
                currentPathPoints.add(event.point)
            }

            MainScreenEvent.Redo -> {
                redoStack.removeLastOrNull()?.let { pathInfo ->
                    paths.add(pathInfo)
                }
            }

            MainScreenEvent.Undo -> {
                paths.removeLastOrNull()?.let { pathInfo ->
                    redoStack.add(pathInfo)
                }
            }

            is MainScreenEvent.ColorSelected -> {
                if (state.value.additionalToolsState == AdditionalToolsState.Hidden) return
                _state.update {
                    it.copy(
                        selectedColor = event.color,
                        additionalToolsState = AdditionalToolsState.Hidden,
                        interactionType = InteractionType.Drawing,
                    )
                }
            }

            MainScreenEvent.ColorSelectorClicked -> {
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
                val additionalToolsState =
                    state.value.additionalToolsState as? AdditionalToolsState.ColorSelector ?: return
                _state.update {
                    it.copy(
                        additionalToolsState = additionalToolsState.copy(
                            isExpanded = !additionalToolsState.isExpanded,
                        )
                    )
                }
            }

            MainScreenEvent.WidthSelectorClicked -> {
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
                _state.update {
                    it.copy(selectedWidthPx = event.widthPx)
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