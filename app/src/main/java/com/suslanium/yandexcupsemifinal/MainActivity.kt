package com.suslanium.yandexcupsemifinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.suslanium.yandexcupsemifinal.ui.screens.main.MainScreen
import com.suslanium.yandexcupsemifinal.ui.theme.YandexCupSemifinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YandexCupSemifinalTheme {
                MainScreen()
            }
        }
    }
}
