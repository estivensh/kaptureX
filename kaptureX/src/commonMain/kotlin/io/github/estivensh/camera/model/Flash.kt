package io.github.estivensh.camera.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.estivensh.state.FlashMode

enum class Flash(
    val drawableRes: ImageVector,
    val contentRes: String,
) {
    Off(Icons.Default.FlashOff, "flash_off"),
    On(Icons.Default.FlashOn, "flash_on"),
    Auto(Icons.Default.FlashAuto, "flash_auto"),
    Always(Icons.Default.FlashOn, "flash_always");

    companion object {
        fun getCurrentValues(isVideo: Boolean) = when {
            isVideo -> listOf(Off, Always)
            else -> entries
        }
    }
}

expect fun Flash.toFlashMode(): FlashMode

expect fun FlashMode.toFlash(isTorchEnabled: Boolean): Flash