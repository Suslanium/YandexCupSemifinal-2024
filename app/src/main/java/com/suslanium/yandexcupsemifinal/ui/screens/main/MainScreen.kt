package com.suslanium.yandexcupsemifinal.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.BottomToolbar
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.ColorSelector
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.DrawZone
import com.suslanium.yandexcupsemifinal.ui.screens.main.components.TopToolbar

@Composable
fun MainScreen() {
    val density = LocalDensity.current
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            defaultLineWidthPx = with(density) { 10.dp.toPx() },
            defaultColor = Color.Red,
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
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
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(32.dp))
            DrawZone(
                state = state,
                onEvent = viewModel::processEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp)),
            )
            Spacer(modifier = Modifier.height(16.dp))
            BottomToolbar(
                state = state,
                onEvent = viewModel::processEvent,
            )
        }
        ColorSelector(
            state = state,
            onEvent = viewModel::processEvent,
        )
    }
}