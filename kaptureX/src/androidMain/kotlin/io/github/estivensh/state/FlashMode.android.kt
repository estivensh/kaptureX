package io.github.estivensh.state

import androidx.camera.core.ImageCapture

actual enum class FlashMode(val mode: Int) {
    On(ImageCapture.FLASH_MODE_ON),
    Auto(ImageCapture.FLASH_MODE_AUTO),
    Off(ImageCapture.FLASH_MODE_OFF);

    val inverse: FlashMode
        get() = when (this) {
            On -> Off
            else -> On
        }

    companion object {
        internal fun find(mode: Int) = entries.firstOrNull { it.mode == mode } ?: Off
    }
}

