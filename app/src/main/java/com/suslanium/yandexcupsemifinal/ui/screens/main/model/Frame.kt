package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

interface UiFrame {
    val paths: List<PathInfo>
    val redoStack: List<PathInfo>
}

class Frame : UiFrame {
    private val mutablePaths: SnapshotStateList<PathInfo> = mutableStateListOf()
    private val mutableRedoStack: SnapshotStateList<PathInfo> = mutableStateListOf()

    override val paths: List<PathInfo>
        get() = mutablePaths

    override val redoStack: List<PathInfo>
        get() = mutableRedoStack

    fun addPath(state: MainScreenState) {
        val pathInfo = createPathInfo(state)
        mutablePaths.add(pathInfo)
        mutableRedoStack.clear()
    }

    fun undo() {
        mutablePaths.removeLastOrNull()?.let { path ->
            mutableRedoStack.add(path)
        }
    }

    fun redo() {
        mutableRedoStack.removeLastOrNull()?.let { path ->
            mutablePaths.add(path)
        }
    }

    fun copy(): Frame {
        val newFrame = Frame()
        newFrame.mutablePaths.addAll(mutablePaths.map { it.copy() })
        newFrame.mutableRedoStack.addAll(mutableRedoStack.map { it.copy() })
        return newFrame
    }
}