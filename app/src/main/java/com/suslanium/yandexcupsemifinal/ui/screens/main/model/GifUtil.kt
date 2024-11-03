package com.suslanium.yandexcupsemifinal.ui.screens.main.model

import android.content.Context
import android.net.Uri
import com.waynejo.androidndkgif.GifEncoder
import java.io.File

fun Context.saveGif(
    uri: Uri,
    frames: LongList<Frame>,
    canvasWidth: Int,
    canvasHeight: Int,
    frameRate: Int,
) {
    val frameDelay = 1000 / frameRate
    val tempFile = File(cacheDir, "temp.gif")
    val encoder = GifEncoder()
    encoder.init(
        canvasWidth,
        canvasHeight,
        tempFile.path,
        GifEncoder.EncodingType.ENCODING_TYPE_SIMPLE_FAST
    )
    frames.forEach { frame ->
        encoder.encodeFrame(frame.toBitmap(canvasWidth, canvasHeight), frameDelay)
    }
    encoder.close()
    contentResolver.openOutputStream(uri)?.use { outputStream ->
        tempFile.inputStream().use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    tempFile.delete()
}