package com.suslanium.yandexcupsemifinal.ui.screens.main

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.BottomToolbar
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.FullScreenLoadingIndicator
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.SpeedSelector
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.SwipeIndicators
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.TopPopupMenu
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.TopToolbar
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.WidthSelector
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.ColorSelector
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.framepager.FramePager
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.InteractionBlock
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEffect
import com.suslanium.yandexcupsemifinal.ui.screens.main.model.MainScreenEvent

@Composable
fun MainScreen() {
    val density = LocalDensity.current
    val context = LocalContext.current
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            defaultLineWidthPx = with(density) { 10.dp.toPx() },
            defaultColor = Color.Red,
            applicationContext = context.applicationContext,
        )
    )
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.data?.let { uri ->
            viewModel.processEvent(MainScreenEvent.ExportToGif(uri))
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle(
        //https://issuetracker.google.com/issues/336842920#comment8
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current,
    )

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is MainScreenEffect.ChooseFileLocation -> {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = MainViewModel.GIF_MIME_TYPE
                        putExtra(Intent.EXTRA_TITLE, MainViewModel.ANIMATION_FILE_NAME)
                    }
                    launcher.launch(intent)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopToolbar(
                state = state,
                onEvent = viewModel::processEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(32.dp))
            FramePager(
                state = state,
                onEvent = viewModel::processEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            BottomToolbar(
                state = state,
                onEvent = viewModel::processEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }
        ColorSelector(
            state = state,
            onEvent = viewModel::processEvent,
        )
        WidthSelector(
            state = state,
            onEvent = viewModel::processEvent,
        )
        TopPopupMenu(
            state = state,
            onEvent = viewModel::processEvent,
        )
        SpeedSelector(
            state = state,
            onEvent = viewModel::processEvent,
        )
        SwipeIndicators(state = state)
    }

    if (state.interactionBlock == InteractionBlock.GifSaving ||
        state.interactionBlock == InteractionBlock.FrameGeneration) {
        FullScreenLoadingIndicator()
    }
}