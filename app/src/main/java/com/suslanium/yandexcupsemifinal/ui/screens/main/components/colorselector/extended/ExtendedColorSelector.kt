package com.suslanium.yandexcupsemifinal.ui.screens.main.components.colorselector.extended

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.suslanium.yandexcupsemifinal.R
import android.graphics.Color as AndroidColor

@Composable
fun ExtendedColorSelector(
    initColor: Color,
    onColorSet: (Color) -> Unit,
    onCancel: () -> Unit,
) {
    val convertedColor = initColor.convert(ColorSpaces.Srgb)
    val hsv = FloatArray(3)
    AndroidColor.RGBToHSV(
        (convertedColor.red * 255).toInt(),
        (convertedColor.green * 255).toInt(),
        (convertedColor.blue * 255).toInt(),
        hsv,
    )
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var saturation by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    HueSlider(
        hue = hue,
        onHueChanged = { hue = it },
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)),
    )
    Spacer(modifier = Modifier.height(16.dp))
    SaturationSlider(
        hue = hue,
        saturation = saturation,
        onSaturationChanged = { saturation = it },
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)),
    )
    Spacer(modifier = Modifier.height(16.dp))
    ValueSlider(
        hue = hue,
        saturation = saturation,
        value = value,
        onValueChanged = { value = it },
        modifier = Modifier
            .height(32.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Row {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onCancel,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onColorSet(Color.hsv(hue, saturation, value)) },
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}