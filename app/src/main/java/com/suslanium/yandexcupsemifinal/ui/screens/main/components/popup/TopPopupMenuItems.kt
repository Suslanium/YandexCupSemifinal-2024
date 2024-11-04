package com.suslanium.yandexcupsemifinal.ui.screens.main.components.popup

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.suslanium.yandexcupsemifinal.R
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent

data class TopPopupMenuItem(
    @DrawableRes val iconRes: Int,
    @StringRes val titleRes: Int,
    val onClickEvent: MainScreenEvent,
)

val topPopupMenuItems = listOf(
    TopPopupMenuItem(
        iconRes = R.drawable.ic_delete_frame,
        titleRes = R.string.delete_all_frames,
        onClickEvent = MainScreenEvent.DeleteAllFramesClicked,
    ),
    TopPopupMenuItem(
        iconRes = R.drawable.ic_copy,
        titleRes = R.string.duplicate_frame,
        onClickEvent = MainScreenEvent.DuplicateFrameClicked,
    ),
    TopPopupMenuItem(
        iconRes = R.drawable.ic_speed,
        titleRes = R.string.change_speed,
        onClickEvent = MainScreenEvent.SpeedSelectorClicked,
    ),
    TopPopupMenuItem(
        iconRes = R.drawable.ic_frame_gen,
        titleRes = R.string.generate_frames,
        onClickEvent = MainScreenEvent.FrameGenerationClicked,
    ),
    TopPopupMenuItem(
        iconRes = R.drawable.ic_gif,
        titleRes = R.string.export_gif,
        onClickEvent = MainScreenEvent.ExportToGifClicked,
    ),
)