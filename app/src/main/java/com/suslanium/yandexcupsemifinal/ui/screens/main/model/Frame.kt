package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

interface UiFrame {
    val paths: List<PathInfo>
    val redoStack: List<PathInfo>
}

data class Frame(
    val mutablePaths: SnapshotStateList<PathInfo> = mutableStateListOf(),
    val mutableRedoStack: SnapshotStateList<PathInfo> = mutableStateListOf(),
) : UiFrame {
    override val paths: List<PathInfo>
        get() = mutablePaths

    override val redoStack: List<PathInfo>
        get() = mutableRedoStack
}